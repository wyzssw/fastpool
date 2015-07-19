package com.justdebugit.fastpool.benchmark;

import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.justdebugit.fastpool.ObjectFactory;
import com.justdebugit.fastpool.Pool;
import com.justdebugit.fastpool.ScalableFastPool;

public class FastpoolVsCommonsPool {
	
	/**
	 * commons-pool 工厂方法
	 * @return
	 */
	public static ObjectPool<SimpleDateFormat> getCommonPool() {
		GenericObjectPool<SimpleDateFormat> commonPool = new GenericObjectPool<SimpleDateFormat>(
				new BasePooledObjectFactory<SimpleDateFormat>() {

					@Override
					public SimpleDateFormat create() throws Exception {
						return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					}

					@Override
					public PooledObject<SimpleDateFormat> wrap(
							SimpleDateFormat arg0) {
						return new DefaultPooledObject<SimpleDateFormat>(arg0);
					}
				});

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(20);
		config.setMaxTotal(20);
		config.setMaxWaitMillis(Integer.MAX_VALUE);
		config.setMinIdle(5);
		commonPool.setConfig(config);
		return commonPool;
	}
	
	/**
	 * fastpool 工厂方法
	 * @return
	 */
	public static Pool<SimpleDateFormat> getFastpool() {
		Pool<SimpleDateFormat> fastPool = new ScalableFastPool<SimpleDateFormat>(
				5, 20, new ObjectFactory<SimpleDateFormat>() {

					@Override
					public SimpleDateFormat makeObject() {
						return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					}

					@Override
					public void destroyObject(SimpleDateFormat simpleDateFormat) throws Exception {
					}
				});
		return fastPool;
	}
			
	
	
	enum Type{
		fastpool,commonpool;
	}
	
	interface PoolApi<T>{
		T get() throws Exception;
		void release(T t) throws Exception;
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		final ObjectPool<SimpleDateFormat> objectPool = getCommonPool();
		final Pool<SimpleDateFormat> fastPool = getFastpool();
		
		//test for commons-pool
		System.out.println("commons-pool has consumed :");
		testConsumeTime(new PoolApi<SimpleDateFormat>() {

			@Override
			public SimpleDateFormat get() throws NoSuchElementException,
					IllegalStateException, Exception {
				return objectPool.borrowObject();
			}

			@Override
			public void release(SimpleDateFormat t) throws Exception {
				objectPool.returnObject(t);
			}
		});
		
		//test For fastpool
		System.out.println("fastpool has consumed :");
		testConsumeTime(new PoolApi<SimpleDateFormat>() {

			@Override
			public SimpleDateFormat get() throws InterruptedException {
				return fastPool.get();
			}

			@Override
			public void release(SimpleDateFormat t) {
				fastPool.release(t);
			}
		});
	}

	public static void testConsumeTime(final PoolApi<SimpleDateFormat> poolApi)  {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		final AtomicLong countLong  = new AtomicLong(10000000);
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
						SimpleDateFormat dateFormat = null;
					    try {
					    	dateFormat = poolApi.get();
							if (countLong.decrementAndGet()==100) {
								System.out.println(System.currentTimeMillis()-atomicLong.get()+" ms");
								shutdownLatch.countDown();
								break;
							};
							if (countLong.get()<=100) {
								break;
							}
//							System.out.println(fastPool.size());
//							System.out.println(dataFormat.format(new Date()));
//							System.out.println(dataFormat.parse("2013-05-25 11:21:21"));
						} catch (InterruptedException e) {
							//ignore
						}catch (Exception e) {
							e.printStackTrace();
						}
					    finally{
							if (dateFormat!=null) {
								try {
									poolApi.release(dateFormat);
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
