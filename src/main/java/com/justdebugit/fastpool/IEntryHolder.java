package com.justdebugit.fastpool;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * @author justdebugit@gmail.com
 *
 * @param <V> 最终操作对象
 */
public interface IEntryHolder<V> {
	   int STATE_NOT_IN_USE = 0;
	   int STATE_IN_USE = 1;
	   int STATE_REMOVED = -1;

	   AtomicInteger state();
	   V          get();
	   void    set(V v);
}
