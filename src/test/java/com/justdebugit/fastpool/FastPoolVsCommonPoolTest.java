package com.justdebugit.fastpool;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.justdebugit.fastpool.pool.FastPoolConfig;
import com.justdebugit.fastpool.pool.GenericFastPool;
import com.justdebugit.fastpool.pool.ObjectFactory;
import com.justdebugit.fastpool.pool.Pool;

import redis.clients.jedis.Jedis;


public class FastPoolVsCommonPoolTest {
  
  
  /**
   * commons-pool 
   * @return
   */
  public static ObjectPool<Jedis> getJedisCommonPool() {
    GenericObjectPool<Jedis> commonPool = new GenericObjectPool<Jedis>(
        new JedisFactory("127.0.0.1", 6379, 10000,
            10000, null, 0, null),new GenericObjectPoolConfig());

      return commonPool;
  }
  
  /**
   * fastpool 
   * @return
   */
  public static Pool<Jedis> getJedisSuperFastpool() {
      FastPoolConfig poolConfig = new FastPoolConfig();
      poolConfig.setMinIdle(1);
      poolConfig.setDisableEvict(true);
      poolConfig.setMaxSize(5);
      Pool<Jedis> fastPool = new GenericFastPool<Jedis>(poolConfig, new ObjectFactory<Jedis>() {

        @Override
        public Jedis makeObject() {
           return new Jedis();
        }

        @Override
        public void destroyObject(Jedis v) throws Exception {
        }

        @Override
        public boolean validateObject(Jedis v) {
          return true;
        } });
      return fastPool;
  }
  
  
  interface PoolApi<T>{
      T get() throws Exception;
      void release(T t) throws Exception;
  }
  
  
  public static void main(String[] args) throws InterruptedException {
      final ObjectPool<Jedis> objectPool = getJedisCommonPool();
      final Pool<Jedis> fastPool = getJedisSuperFastpool();
      
      
      System.out.println("commons-pool has consumed :");
      testConsumeTime(new PoolApi<Jedis>() {

          @Override
          public Jedis get() throws NoSuchElementException,
                  IllegalStateException, Exception {
              return objectPool.borrowObject();
          }

          @Override
          public void release(Jedis t) throws Exception {
              objectPool.returnObject(t);
          }
      });
      
    //test For fastpool
      System.out.println("superfastpool has consumed :");
      testConsumeTime(new PoolApi<Jedis>() {

          @Override
          public Jedis get() throws InterruptedException {
              return fastPool.get();
          }

          @Override
          public void release(Jedis t) {
              fastPool.release(t);
          }
      });
      
  }

  public static void testConsumeTime(final PoolApi<Jedis> poolApi)  {
      ExecutorService executorService = Executors.newFixedThreadPool(10);
      final AtomicLong countLong  = new AtomicLong(10000);
      final CountDownLatch latch = new CountDownLatch(1);
      final CountDownLatch shutdownLatch = new CountDownLatch(1);
      final AtomicLong atomicLong = new AtomicLong();
      for (int i = 0; i < 10; i++) {
          executorService.submit(new Runnable() {
              
              @Override
              public void run() {
                  try {
                      latch.await();
                  } catch (InterruptedException e1) {
                      e1.printStackTrace();
                  }
                  atomicLong.compareAndSet(0, System.currentTimeMillis());
                  while (true) {
                    Jedis jedis = null;
                      try {
                          jedis = poolApi.get();
                          Thread.sleep(1);//
                          if (countLong.decrementAndGet()==100) {
                              System.out.println(System.currentTimeMillis()-atomicLong.get()+" ms");
                              shutdownLatch.countDown();
                              break;
                          };
                          if (countLong.get()<=100) {
                              break;
                          }
                      } catch (InterruptedException e) {
                          //ignore
                      }catch (Exception e) {
                          e.printStackTrace();
                      }
                      finally{
                          if (jedis!=null) {
                              try {
                                  poolApi.release(jedis);
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          }
                      }
                  }
                  
              }
          });
      }
      latch.countDown();
      try {
          shutdownLatch.await();
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      executorService.shutdownNow();
  }



}
