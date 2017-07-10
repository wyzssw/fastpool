package com.justdebugit.fastpool.codis;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.justdebugit.fastpool.pool.MultiShardPool;
import com.justdebugit.fastpool.pool.Pool;
import com.justdebugit.fastpool.pool.MultiShardPool.ShardedObjectFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class CodisPool implements Pool<Jedis> {

  private MultiShardPool<Jedis> internalPool;
  
  private CodisConfig codisConfig;

  public CodisPool(CodisConfig config,List<String> addrs) {
    
    this.codisConfig = config;
    this.internalPool = new MultiShardPool<>(config.getPoolConfig(), addrs, new ShardedObjectFactory<Jedis>() {

      @Override
      public void destroyObject(Jedis v) throws Exception {
         v.close();
      }

      @Override
      public boolean validateObject(Jedis v) {
        v.ping();
        return true;
      }

      @Override
      public Jedis makeObject(String address) {
         return new Jedis(address.split(":")[0], Integer.parseInt(address.split(":")[1]), config.getConnectionTimeout(), config.getSoTimeout());
       }
      });
  }

  @Override
  public void close() throws IOException {
    internalPool.close();
  }
  
  public Jedis getInTime() {
    Jedis jedis = null;
    try {
      jedis = internalPool.get(codisConfig.getPoolConfig().getMaxWaitMs(), TimeUnit.MILLISECONDS);
    }catch (Exception e) {
      throw new JedisConnectionException("Could not get a resource from the pool", e);
    }
    return jedis;
  }

  @Override
  public Jedis get() throws InterruptedException {
    return internalPool.get();
  }

  @Override
  public Jedis get(long timeout, TimeUnit timeUnit) throws InterruptedException, TimeoutException {
    return internalPool.get(timeout, timeUnit);
  }

  @Override
  public void release(Jedis t, boolean broken) {
    internalPool.release(t, broken);
  }

  @Override
  public void release(Jedis t) {
    internalPool.release(t,false);
  }

  @Override
  public int size() {
    return internalPool.size();
  }


 

}
