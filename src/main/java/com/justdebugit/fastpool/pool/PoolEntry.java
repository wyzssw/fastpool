package com.justdebugit.fastpool.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 
 * @author wanghongfeng
 *
 * @param <T>
 */
public class PoolEntry<T> implements IEntry {
  private volatile T value;
  private final GenericFastPool<T> pool;
  private final AtomicInteger state;
  private AtomicLong  lastRetureTime;
  
  
  public T get(){
    return value;
  }


  public PoolEntry(T t, GenericFastPool<T> pool) {
    this.value = t;
    this.pool =  pool;
    this.state = new AtomicInteger(States.STATE_NOT_IN_USE.stateVal);
    this.lastRetureTime = new AtomicLong(System.currentTimeMillis());
  }

  @Override
  public States state() {
    return States.findByValue(state.get());
  }
  
  public boolean compareAndSet(States expect,States update) {
    boolean result = state.compareAndSet(expect.stateVal, update.stateVal);
    if (update == States.STATE_IN_USE) {
      lastRetureTime.set(System.currentTimeMillis());
    }
    return result;
  }
  
  public Long idleTime(){
    return System.currentTimeMillis() - lastRetureTime.get();
  }
  
  public void renew(){
    lastRetureTime.set(System.currentTimeMillis());
  }

  public T getTalue() {
    return value;
  }


  public void setTalue(T value) {
    this.value = value;
  }


  public GenericFastPool<T> getPool() {
    return pool;
  }


  public AtomicInteger getState() {
    return state;
  }


  @Override
  public String toString() {
    return "DefaultEntryHolder [value=" + value + ", state=" + state + "]";
  }


}
