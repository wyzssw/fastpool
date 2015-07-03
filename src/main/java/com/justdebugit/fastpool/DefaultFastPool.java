package com.justdebugit.fastpool;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * 
 * @author justdebugit@gmail.com
 *
 * @param <V>
 */
public class DefaultFastPool<V> extends FastPool<DefaultEntryHolder<V>, V> implements PoolOperation<V> {

	private  final ThreadLocal<WeakReference<DefaultEntryHolder<V>>> holderContext = new ThreadLocal<WeakReference<DefaultEntryHolder<V>>>();
	private  ObjectFactory<V> objectFactory ;
	
	public  DefaultFastPool(int size,ObjectFactory<V> objectFactory) {
		this.objectFactory = objectFactory;
		for (int i = 0; i < size; i++) {
			super.add(new DefaultEntryHolder<V>(objectFactory.makeObject()));
		}
	}
	
	
	public interface ObjectFactory<V>{
		//object must not be null
		V  makeObject();
	}
	
	@Override
	public V get() throws InterruptedException {
	    try {
			return get(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			return null;//just ignore,not happen
		}
	}
	
	
	@Override
	public V get(long timeout, TimeUnit timeUnit) throws InterruptedException,TimeoutException {
		DefaultEntryHolder<V> holder = super.borrow(timeout, timeUnit);
		if (holder==null) {
			throw new TimeoutException();
		}
		holderContext.set(new WeakReference<DefaultEntryHolder<V>>(holder));
		return holder.get();
	}
	
	
	
	@Override
	public void release(V value,boolean broken) {
		assert value!=null;
		WeakReference<DefaultEntryHolder<V>> holderReference = null;
		if ((holderReference=holderContext.get())!=null) {
			holderContext.remove();
			if (broken) {
				super.replaceAndrequit(holderReference.get(), objectFactory.makeObject());
			}else {
				super.requite(holderReference.get());
			}
			
		}
	}

	@Override
	public void scale(int size) {
		for (int i = 0; i <size; i++) {
			super.add(new DefaultEntryHolder<V>(objectFactory.makeObject()));
		}
	}


	@Override
	public void release(V value) {
		assert value!=null;
		WeakReference<DefaultEntryHolder<V>> holderReference = null;
		if ((holderReference=holderContext.get())!=null) {
			holderContext.remove();
			super.requite(holderReference.get());
		}
		
	}




	


	
}
