package com.justdebugit.fastpool.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.justdebugit.fastpool.DefaultFastPool;
import com.justdebugit.fastpool.ObjectFactory;


public class FastPoolCorrectTest {

	public static void main(String[] args) throws InterruptedException {
		ExecutorService executorService = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());
		final DefaultFastPool<List<Integer>> fastPool = new DefaultFastPool<List<Integer>>(5,new ObjectFactory<List<Integer>>() {

			@Override
			public List<Integer> makeObject() {
				  List<Integer> list = new ArrayList<Integer>();
				  list.add(0);
				  return list;
			}

			@Override
			public void destroyObject(List<Integer> list) throws Exception {
			}
		});
		final AtomicLong atomicLong = new AtomicLong();
		final CountDownLatch latch = new CountDownLatch(1);
		final int loopCount = 30000006;
		for (int i = 0; i < loopCount; i++) {
			executorService.submit(new Runnable() {
				
				@Override
				public void run() {    
					List<Integer> value = null;
					boolean broken = false;
					try {
						value  = fastPool.get();
						System.out.println(fastPool.getSeq());
						value.set(0, value.get(0) + 1);
//						Thread.sleep(1);
						if (atomicLong.incrementAndGet()>loopCount-6 && latch.getCount()==1) {
							latch.countDown();
						}
					} catch (Exception e) {
						e.printStackTrace();
						broken = true;
					}finally{
						if (value!=null) {
							fastPool.release(value,broken);
						}
					}
				}
			});
		}
		long start = System.currentTimeMillis();
		latch.await();
		long end = System.currentTimeMillis();
		System.out.println(end-start);
		Thread.sleep(1000);
		fastPool.dumpState();//验证总和是否等于30000006,
		/**
		 * 0 打印：
			DefaultEntryHolder [value=[8829912], state=0]
			DefaultEntryHolder [value=[6830107], state=0]
			DefaultEntryHolder [value=[5655794], state=0]
			DefaultEntryHolder [value=[4782550], state=0]
			DefaultEntryHolder [value=[3901643], state=0]
			按index使用次数依次降低，有优化空间
		 */
	}
}
