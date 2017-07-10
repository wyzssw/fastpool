package com.justdebugit.fastpool.pool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class MultiShardPool<T> implements Pool<T>{
  
  private static final Logger logger = LoggerFactory.getLogger(MultiShardPool.class);
  
  private ConcurrentMap<String, ShardedPool> shardMap = new ConcurrentHashMap<>();
  
  private ConcurrentMap<IdentityWrapper<T>, ShardedPool> entryPoolCache = new ConcurrentHashMap<>();
  
  private CopyOnWriteArraySet<String> normalList = new CopyOnWriteArraySet<>();
  
  private LoadBalance loadBalance  = new RandomLoadBalance();
  
  private static final Integer MAX_FAIL_COUNT = 5;
  
  private static final ScheduledExecutorService CHECK_EXECUTOR = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
    
    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread();
      thread.setDaemon(true);
      thread.setName("check-server-thread");
      return thread;
    }
  });
  
  public  MultiShardPool(FastPoolConfig config,List<String> addressList,ShardedObjectFactory<T> shardedObjectFactory) {
    assert addressList.size() > 0;
    addressList.forEach(address -> {
      FastPoolConfig newConfig = new FastPoolConfig(config);
      newConfig.setDisableEvict(true);
      newConfig.setPoolName(address);
      shardedObjectFactory.setAddress(address);
      GenericFastPool<T> pool = new GenericFastPool<>(newConfig, shardedObjectFactory);
      shardMap.put(address, new ShardedPool(pool,address));
      normalList.add(address);
    });
    CHECK_EXECUTOR.scheduleWithFixedDelay(CHECK_SERVER, 3, 3, TimeUnit.SECONDS);
  }
  
  
  public Runnable CHECK_SERVER = new Runnable() {
    
    @Override
    public void run() {
      shardMap.values().forEach(shard -> {
        if (shard.available() || shard.suspectable()) {
           shard.internalPool.doEvict();
        }else {
          shard.tryRecover();
        }
      });
    }
  };
  
  public interface LoadBalance {
    String   select(List<String> list);
  }
  
  public static class RandomLoadBalance implements LoadBalance {

    @Override
    public String select(List<String> list) {
      if (list.size() < 1) {
        throw new IllegalStateException(" pool size must greater than one");
      }
      return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
  }

  

  @Override
  public T get() throws InterruptedException {
    ShardedPool pool  = getPool();
    if (pool==null) {
        throw new IllegalStateException("there is no pool available");
    }
    T t = pool.internalPool.get();
    entryPoolCache.put(new IdentityWrapper<T>(t), pool);
    return t;
  }

  private ShardedPool getPool() {
    ShardedPool pool = null;
    for (int i = 0; i < 2; i++) {
      String key = loadBalance.select(new ArrayList<>(normalList));
      pool = shardMap.get(key);
      if (pool.unavailable()) {
        continue;
      }else {
        break;
      }
    }
    return pool;
  }

  @Override
  public T get(long timeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
    ShardedPool pool  = getPool();
    if (pool==null) {
        throw new IllegalStateException("there is no pool available");
    }
    T t = pool.internalPool.get(timeout,timeUnit);
    entryPoolCache.put(new IdentityWrapper<T>(t), pool);
    return t;
  }

  @Override
  public void release(T t, boolean broken) {
    ShardedPool shardedPool = entryPoolCache.remove(new IdentityWrapper<T>(t));
    if (shardedPool != null) {
        shardedPool.internalPool.release(t,broken);
        if (broken) {
          shardedPool.fail();
        }
    }else{
      throw new IllegalArgumentException("The entry is not return by this Pool");
    }
  }

  @Override
  public void release(T t) {
    release(t, false);
  }
  
  @Override
  public void close() throws IOException {
    shardMap.values().forEach(t -> {
      try {
        t.internalPool.close();
      } catch (IOException e) {
        logger.error(e.getMessage(),e);
      }
    });
  }

  @Override
  public int size() {
    int sum = 0;
    ConcurrentMap<String, ShardedPool> map  = shardMap;
    for (Map.Entry<String, ShardedPool> entry : map.entrySet()) {
         sum += entry.getValue().internalPool.size();
    }
    return sum;
  }
  

  public enum ShardedStates{
    AVAILABLE(0),SUSPECTABLE(1),UNAVAILABLE(2);
    
    public final Integer value;
    
    private ShardedStates(Integer value){
      this.value = value;
    }
    
    public static ShardedStates findByValue(int value) { 
      switch (value) {
        case 0:
          return AVAILABLE;
        case 1:
          return SUSPECTABLE;
        case 2:
          return UNAVAILABLE;
        default:
          return null;
      }
    }
  }
  
  public  class ShardedPool {
    
    private AtomicInteger stateVal = new AtomicInteger(ShardedStates.AVAILABLE.value);
    
    private String shardedAddr;
    
    private AtomicInteger failCount = new AtomicInteger(0);
    
    private final GenericFastPool<T> internalPool;
    
    public ShardedPool(GenericFastPool<T> pool,String shardedAddr){
      this.internalPool = pool;
      this.shardedAddr = shardedAddr;
    }
    
    public GenericFastPool<T> getInternalPool(){
      return internalPool;
    }
    
    public void fail(){
      if (normalList.size() <= shardMap.size()/3 || normalList.size() <=1 || stateVal.get() == ShardedStates.UNAVAILABLE.value) {
         return;
      }
      if (failCount.incrementAndGet() < MAX_FAIL_COUNT) {
          stateVal.compareAndSet(ShardedStates.AVAILABLE.value, ShardedStates.SUSPECTABLE.value);
      }else if (failCount.get() >= MAX_FAIL_COUNT) {
        if (stateVal.compareAndSet(ShardedStates.AVAILABLE.value, ShardedStates.UNAVAILABLE.value)
            || stateVal.compareAndSet(ShardedStates.SUSPECTABLE.value,
                ShardedStates.UNAVAILABLE.value)) {
           normalList.remove(this.shardedAddr);
           internalPool.destroyIdel();
        }
      }
    }
    
    
    public boolean tryRecover(){
      T t = null;
      try {
        t =  internalPool.getObjectFactory().makeObject();
        internalPool.getObjectFactory().validateObject(t);
      } catch (Exception e) {
        return false;
      }finally {
        try {
          internalPool.getObjectFactory().destroyObject(t);
        } catch (Exception e) {}
      }
      stateVal.set(ShardedStates.AVAILABLE.value);
      normalList.add(shardedAddr);
      failCount.set(0);
      return true;
    }
    
    
    public ShardedStates getState() {
      return ShardedStates.findByValue(stateVal.get());
    }
    
    public boolean suspectable(){
      return getState() == ShardedStates.SUSPECTABLE;
    }
    
    public boolean available(){
      return getState() == ShardedStates.AVAILABLE;
    }
    
    
    public boolean unavailable(){
      return getState() == ShardedStates.UNAVAILABLE;
    }
  }
  
  
  public static abstract class ShardedObjectFactory<T> implements ObjectFactory<T>{
    
    private String address;
    
    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }
    
    public abstract T makeObject(String address);

    @Override
    public T makeObject() {
      return makeObject(getAddress());
    }
    
  }

}
