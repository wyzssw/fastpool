package com.justdebugit.fastpool.pool;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.justdebugit.fastpool.pool.FastPool.PoolListener;

/**
 * 
 * @author wanghongfeng
 *
 * @param <T>
 */
public class GenericFastPool<T> implements Pool<T>, PoolListener {
  
  
  private static final Logger logger = LoggerFactory.getLogger(GenericFastPool.class);

  
  private static final int POOL_NORMAL      = 0;
  private static final int POOL_SHUTDOWN    = 3;
  
  
  private final ThreadPoolExecutor addExecutor = createExecutor("add");
  private final ThreadPoolExecutor removeExecutor = createExecutor("remove");
  private final ScheduledExecutorService evictExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory("evict-thread"));;
  private final ConcurrentMap<IdentityWrapper<T>, PoolEntry<T>> entryCache = new ConcurrentHashMap<>();
  
  
  private volatile int poolState;
  
  private final AtomicInteger totalConnections;
  
  private ObjectFactory<T> objectFactory;
  
  private FastPool<PoolEntry<T>> internalPool;
  
  private FastPoolConfig poolConfig;
  
  

  public GenericFastPool(FastPoolConfig poolConfig, ObjectFactory<T> objectFactory) {
    if (poolConfig.getMaxSize() < poolConfig.getMinIdle()) {
      throw new IllegalArgumentException("MaxSize must greater than minIdle");
    }
    this.poolConfig = poolConfig;
    this.objectFactory = objectFactory;
    this.internalPool = new FastPool<>(this);
    this.totalConnections = new AtomicInteger();
    this.poolState = POOL_NORMAL;
    if (!poolConfig.isDisableEvict()) {
       evictExecutor.scheduleAtFixedRate(() -> doEvict(),
          poolConfig.getTimeBetweenEvict(), poolConfig.getTimeBetweenEvict(),
          TimeUnit.MILLISECONDS);
    }
    fillPool();
  }

  
  public ObjectFactory<T> getObjectFactory(){
    return objectFactory;
  }
  

  @Override
  public T get() throws InterruptedException {
    try {
      return get(Integer.MAX_VALUE, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      return null;// just ignore,not happen
    }
  }


  @Override
  public T get(long timeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
    PoolEntry<T> poolEntry = internalPool.borrow(timeout, timeUnit);
    if (poolEntry == null) {
      throw new TimeoutException();
    }
    return poolEntry.get();
  }



  @Override
  public void release(T value, boolean broken) {
    assert value != null;
    if (broken) {
      PoolEntry<T> poolEntry = getPoolEntry(value);
      destroyEntry(poolEntry);
    } else {
      release(value);
    }
  }


  private void destroyEntry(PoolEntry<T> poolEntry) {
    internalPool.remove(poolEntry);
    entryCache.remove(new IdentityWrapper<T>(poolEntry.get()));
    totalConnections.decrementAndGet();
    removeExecutor.execute(new Runnable() {
      
      @Override
      public void run() {
        try {
          objectFactory.destroyObject(poolEntry.get());
        } catch (Throwable e) {}
      }
    });
  }



  private PoolEntry<T> getPoolEntry(T value) {
    PoolEntry<T> poolEntry = entryCache.get(new IdentityWrapper<T>(value));
    if (poolEntry == null) {
      throw new IllegalArgumentException("The entry is not borrowed by pool");
    }
    poolEntry.renew();
    return poolEntry;
  }



  @Override
  public void release(T value) {
    assert value != null;
    PoolEntry<T> poolEntry = getPoolEntry(value);
    internalPool.requite(poolEntry);
  }
  
  
  public void doEvict(){
    internalPool.values(States.STATE_NOT_IN_USE).stream().forEach(v ->{
        if (v.idleTime() > poolConfig.getMinEvictableIdle()  && internalPool.reserve(v) ) {
           destroyEntry(v);
        }
    });
    fillPool();
  }
  
  
  public void destroyIdel(){
    internalPool.values(States.STATE_NOT_IN_USE).stream().forEach(v ->{
        if (internalPool.reserve(v) ) {
           destroyEntry(v);
        }
    });
  }

  @Override
  public synchronized void  close() throws IOException {
    poolState = POOL_SHUTDOWN;
    List<PoolEntry<T>> list = internalPool.values();
    internalPool.close();
    list.forEach(item ->{
      T value = item.get();
      try {
        objectFactory.destroyObject(value);
      } catch (Exception e) {
        logger.error(e.getMessage(),e);
      }
    });
    addExecutor.shutdown();
    removeExecutor.shutdown();
    evictExecutor.shutdown();
  }

  private class PoolEntryCreator implements Callable<Boolean> {
    @Override
    public Boolean call() throws Exception {
      long sleepBackoff = 200L;
      while (poolState == POOL_NORMAL && totalConnections.get() < poolConfig.getMaxSize()) {
        final PoolEntry<T> poolEntry = createPoolEntry();
        if (poolEntry != null) {
          totalConnections.incrementAndGet();
          entryCache.put(new IdentityWrapper<>(poolEntry.get()), poolEntry);
          internalPool.add(poolEntry);
          return Boolean.TRUE;
        }

        quietlySleep(sleepBackoff);
        sleepBackoff = Math.min(TimeUnit.SECONDS.toMillis(10),(long) (sleepBackoff * 1.5));
      }
      return Boolean.FALSE;
    }

    private PoolEntry<T> createPoolEntry() {
      T t = null;
      try {
        t = objectFactory.makeObject();
      } catch (Exception e) {
        logger.error("Pool " + poolConfig.getPoolName() +"get error:" +e.getMessage(),e);
        return null;
      }
      return new PoolEntry<T>(t,GenericFastPool.this);
      
    }
  }
  
  public static void quietlySleep(final long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // I said be quiet!
    }
  }

  @Override
  public int size() {
    return internalPool.size();
  }
  
  
  private static ThreadPoolExecutor createExecutor(String threadName) {
    ThreadFactory threadFactory = threadFactory(threadName);
    return new ThreadPoolExecutor(1, 1, 6, TimeUnit.SECONDS, new LinkedTransferQueue<>(), threadFactory,new ThreadPoolExecutor.DiscardPolicy());
  }

  
  public final int getIdleEntrys() {
    return internalPool.getCount(States.STATE_NOT_IN_USE);
  }

  private void fillPool() {
    int toAdd = Math.min(poolConfig.getMaxSize() - totalConnections.get(),
        poolConfig.getMinIdle() - getIdleEntrys()) - addExecutor.getQueue().size();
    for (int i = 0; i < toAdd; i++) {
        addEntry();
    }
  }


  private static ThreadFactory threadFactory(String threadName) {
    ThreadFactory threadFactory = new ThreadFactory(){
      
      @Override
      public Thread newThread(Runnable r) {
          Thread thread=   new Thread(r);
          thread.setDaemon(true);
          thread.setName(threadName);
          return thread;
      }
    };
    return threadFactory;
  }


  @Override
  public Future<Boolean> addEntry() {
    if (poolState == POOL_NORMAL && totalConnections.get() >= poolConfig.getMaxSize()) {
      return successFuture();
    }
    return addExecutor.submit(new PoolEntryCreator());
  }
  
  private static Future<Boolean> successFuture(){
    return 
    new Future<Boolean>() {

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return true;
      }

      @Override
      public Boolean get() throws InterruptedException, ExecutionException {
        return true;
      }

      @Override
      public Boolean get(long timeout, TimeUnit unit)
          throws InterruptedException, ExecutionException, TimeoutException {
         return true;
      }};
  }
  

}
