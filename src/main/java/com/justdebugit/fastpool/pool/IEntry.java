package com.justdebugit.fastpool.pool;


/**
 * 
 * @author wanghongfeng
 *
 * @param <V> 最终操作对象
 */
public interface IEntry {
  
  States state();
  
  boolean compareAndSet(States expect,States update);
}
