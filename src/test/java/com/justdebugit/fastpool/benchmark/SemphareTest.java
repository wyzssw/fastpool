package com.justdebugit.fastpool.benchmark;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 用作比较
 * @author justdebugit
 *
 */
public class SemphareTest {
	private static Semaphore semaphore = new Semaphore(5);
	private static ReentrantLock lock = new ReentrantLock();
	private static final CountDownLatch latch = new CountDownLatch(1);
	private static final AtomicLong     counter = new AtomicLong();
	private static List<EntryHolder<String>> list = new CopyOnWriteArrayList<SemphareTest.EntryHolder<String>>();
	static{
		list.add(new EntryHolder<String>("o1"));
		list.add(new EntryHolder<String>("o2"));
		list.add(new EntryHolder<String>("o3"));
		list.add(new EntryHolder<String>("o4"));
		list.add(new EntryHolder<String>("o5"));
	}
	private static final int loopCount = 30000006;
	private static Runnable getRunnable(){
		return new Runnable() {
			
			@Override
			public void run() {
				EntryHolder<String> myObjectEntry = null;
				try {
					semaphore.acquire();
//					lock.lock();
					for (EntryHolder<String> objectEntry : list) {
						 if (objectEntry.compareAndSet(0, 1)) {
							myObjectEntry = objectEntry;
//							Thread.sleep(1);
//							System.out.println(objectEntry.get());
							if (counter.incrementAndGet()>loopCount -6 &&latch.getCount()==1) {
								latch.countDown();
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (myObjectEntry!=null) {
						myObjectEntry.compareAndSet(1, 0);
					}
					semaphore.release();
//					lock.unlock();
				}
				
			}
		};
	}

	private static class EntryHolder<T>{
	    private volatile AtomicInteger state = new AtomicInteger(0);
	    private  final T entry;
	    public   EntryHolder(T t) {
	    	this.entry = t;
		}
	    
	    private boolean compareAndSet(int expect,int update){
	    	return state.compareAndSet(expect, update);
	    }
	    
	    private T get(){
	    	return entry;
	    }
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		ExecutorService executorService = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());
		
		
		for (int i = 0; i < loopCount; i++) {
			executorService.submit(getRunnable());
		}
		long start = System.currentTimeMillis();
		latch.await();
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
}
