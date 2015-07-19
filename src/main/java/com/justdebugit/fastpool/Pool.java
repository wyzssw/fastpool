package com.justdebugit.fastpool;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * 池操作
 * @author justdebugit@gmail.com
 *
 * @param <T>
 */
public interface Pool<T> extends Closeable{
	
	/**
	 * 获取一个对象，支持超时
	 * @param timeout
	 * @param timeUnit
	 * @return
	 * @throws InterruptedException
	 */
	public T get() throws InterruptedException;
	
	/**
	 * 获取一个对象，支持超时
	 * @param timeout
	 * @param timeUnit
	 * @return
	 * @throws InterruptedException
	 */
	public T get(long timeout,TimeUnit timeUnit) throws InterruptedException,TimeoutException;
	
	/**
	 * 换回对象并对破损对象做处理
	 * @param t
	 * @param broken
	 */
	public void release(T t,boolean broken);
	
	/**
	 * 还回对象
	 * @param t
	 */
	public void release(T t);
	
	/**
	 * 池大小
	 * @return
	 */
	public int size();
	
	/**
	 * 热扩容
	 * @param size
	 */
    public void scale(int size);

}
