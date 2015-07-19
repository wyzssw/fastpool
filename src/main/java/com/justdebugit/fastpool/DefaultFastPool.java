package com.justdebugit.fastpool;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * 
 * @author justdebugit@gmail.com
 *
 * @param <V>
 */
public class DefaultFastPool<V> extends FastPool<EntryHolder<V>, V> implements Pool<V> {

	private  final ThreadLocal<WeakReference<EntryHolder<V>>> holderContext = new ThreadLocal<WeakReference<EntryHolder<V>>>();
	private  ObjectFactory<V> objectFactory ;
	
	public  DefaultFastPool(int size,ObjectFactory<V> objectFactory) {
		this.objectFactory = objectFactory;
		for (int i = 0; i < size; i++) {
			super.add(new EntryHolder<V>(objectFactory.makeObject()));
		}
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
		EntryHolder<V> holder = super.borrow(timeout, timeUnit);
		if (holder==null) {
			throw new TimeoutException();
		}
		holderContext.set(new WeakReference<EntryHolder<V>>(holder));
		return holder.get();
	}
	
	
	
	@Override
	public void release(V value,boolean broken) {
		assert value!=null;
		WeakReference<EntryHolder<V>> holderReference = null;
		EntryHolder<V> holder = null;
		if ((holderReference=holderContext.get())!=null && (holder=holderReference.get())!=null) {
			holderContext.remove();
			if (broken) {
				super.replaceAndrequit(holder, objectFactory.makeObject());
			}else {
				super.requite(holder);
			}
			
		}
	}

	@Override
	public void scale(int size) {
		for (int i = 0; i <size; i++) {
			super.add(new EntryHolder<V>(objectFactory.makeObject()));
		}
	}


	@Override
	public void release(V value) {
		assert value!=null;
		WeakReference<EntryHolder<V>> holderReference = null;
		EntryHolder<V> holder = null;
		if ((holderReference=holderContext.get())!=null && (holder=holderReference.get())!=null) {
			holderContext.remove();
			super.requite(holder);
		}
		
	}

	@Override
	public void close() throws IOException {
		List<EntryHolder<V>> list =values();
		for (EntryHolder<V> entryHolder : list) {
			 V value =  entryHolder.get();
			 try {
				objectFactory.destroyObject(value);
			} catch (Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
	}
	
}
