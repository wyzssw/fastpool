package com.justdebugit.fastpool;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * @author justdebugit@gmail.com
 *
 * @param <V>
 */
public class DefaultEntryHolder<V> implements IEntryHolder<V>{
	private volatile V value;
	private final AtomicInteger state;
	
	public DefaultEntryHolder(V v){
		this.value = v;
		state = new AtomicInteger();
	}
	
	public  DefaultEntryHolder(V v,int stateInt) {
		this.value = v;
		this.state = new AtomicInteger(stateInt);
	}

	@Override
	public AtomicInteger state() {
		   return state;
	}

	@Override
	public V get() {
		return value;
	}

	@Override
	public void set(V v) {
		assert v !=null;
		value = v;
	}

	@Override
	public String toString() {
		return "DefaultEntryHolder [value=" + value + ", state=" + state + "]";
	}
	

}
