package com.justdebugit.fastpool.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.justdebugit.fastpool.DefaultFastPool;
import com.justdebugit.fastpool.DefaultFastPool.ObjectFactory;
/**
 * 
 * @author justdebugit
 *
 */
public class FastPoolSimpleDateExample {
	
	static final DefaultFastPool<SimpleDateFormat> fastPool = new DefaultFastPool<SimpleDateFormat>(5,new ObjectFactory<SimpleDateFormat>() {

		@Override
		public SimpleDateFormat makeObject() {
			  return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		}
	});
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		int loopCount = 1000;
		for (int i = 0; i < loopCount; i++) {
			executorService.submit(new Runnable() {
				
				@Override
				public void run() {
					SimpleDateFormat dataFormat = null;
				    try {
						dataFormat = fastPool.get();
						System.out.println(dataFormat.format(new Date()));
						System.out.println(dataFormat.parse("2013-05-25 11:21:21"));
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						if (dataFormat!=null) {
							fastPool.release(dataFormat);
						}
					}
				}
			});
		}
	
	}

}
