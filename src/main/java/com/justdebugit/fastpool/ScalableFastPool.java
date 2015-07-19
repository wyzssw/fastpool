package com.justdebugit.fastpool;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 支持对象池最大最小数量，根据竞争情况自动扩容和收缩
 * 合理利用资源
 * @author justdebugit@gmail.com
 *
 * @param <V>
 */
public class ScalableFastPool<V>  implements Pool<V> {
	private  static final int DEFAULT_MIN_SIZE = 5;
	private  static final int DEFAULT_MAX_SIZE = 15;
    private  int minSize;
    private  int maxSize;
	private  ObjectFactory<V> objectFactory;

	private  DefaultFastPool<V> internalPool;
    private  final AtomicBoolean  initFlag = new AtomicBoolean(false);
    private static final ScheduledExecutorService scalableScheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			Thread thread=   new Thread(r);
			thread.setDaemon(true);
			thread.setName("scale-shrink-thread");
			return thread;
		}
	});
	
	
	public  ScalableFastPool(int minSize,int maxSize,ObjectFactory<V> objectFactory) {
		if (minSize>maxSize) {
			throw new IllegalArgumentException("maxSize must greater than minSize");
		}
		this.maxSize = maxSize;
		this.minSize = minSize;
		this.objectFactory = objectFactory;
		init();
	}
	
    public  ScalableFastPool(ObjectFactory<V> objectFactory) {
		this(DEFAULT_MIN_SIZE,DEFAULT_MAX_SIZE, objectFactory);
	}
    
    public  ScalableFastPool() {
    	init();
    }
    
    
    private  void init(){
    	assert objectFactory !=null && minSize >0 && minSize <= maxSize;
    	if (initFlag.compareAndSet(false, true)) {
    		internalPool = new DefaultFastPool<V>(minSize,objectFactory);
			scalableScheduler.scheduleWithFixedDelay(new Runnable() {
				boolean bool = false;
				
				@Override
				public void run() {
					int maxSize = getMaxSize();
					int size    = getInternalPool().size();
					int pendintCount  = getInternalPool().getPendingQueue();
					if (size<maxSize && pendintCount>0) {
						internalPool.scale(Math.min(maxSize-size, pendintCount));
					}
					if (bool) {
						getInternalPool().tryShrink();
					}
					bool = !bool;
				}
			}, 1, 2, TimeUnit.SECONDS);
		}
    	
    }
	
	@Override
	public V get() throws InterruptedException {
		return internalPool.get();
	}

	@Override
	public V get(long timeout, TimeUnit timeUnit)
			throws InterruptedException, TimeoutException {
		return internalPool.get(timeout, timeUnit);
	}

	@Override
	public void release(V t, boolean broken) {
		internalPool.release(t, broken);
	}

	@Override
	public void release(V t) {
		internalPool.release(t);
	}

	@Override
	public void scale(int size) {
		internalPool.scale(size);
	}
	

	@Override
	public int size() {
		return internalPool.size();
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
		 
	public ObjectFactory<V> getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(ObjectFactory<V> objectFactory) {
		this.objectFactory = objectFactory;
	}

	public DefaultFastPool<V> getInternalPool() {
		return internalPool;
	}

	@Override
	public void close() throws IOException {
		internalPool.close();
	}

}
