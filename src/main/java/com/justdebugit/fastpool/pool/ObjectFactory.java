package com.justdebugit.fastpool.pool;

/**
 * 
 * @author wanghongfeng
 *
 * @param <V>
 */
public interface ObjectFactory<T> {
  // object must not be null
  T makeObject();

  void     destroyObject(T v) throws Exception;
  
  boolean  validateObject(T v);
}
