package com.justdebugit.fastpool.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;

import com.justdebugit.fastpool.DefaultFastPool;
import com.justdebugit.fastpool.ObjectFactory;
import com.justdebugit.fastpool.Pool;
/**
 * 
 * @author justdebugit
 *
 */
public class FastPoolJedisExample {
	
	static final Pool<Jedis> fastPool = new DefaultFastPool<Jedis>(5,new ObjectFactory<Jedis>() {

		@Override
		public Jedis makeObject() {
			   return new Jedis("127.0.0.1", 6379);
		}
	});
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		int loopCount = 1000;
		for (int i = 0; i < loopCount; i++) {
			executorService.submit(new Runnable() {
				
				@Override
				public void run() {
					Jedis jedis = null;
				    try {
						jedis = fastPool.get();
						System.out.println(jedis.get("key1"));
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if (jedis!=null) {
							fastPool.release(jedis);
						}
					}
				}
			});
		}
	
	}

}
