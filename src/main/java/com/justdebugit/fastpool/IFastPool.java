package com.justdebugit.fastpool;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface IFastPool<T extends IEntryHolder<V>, V> {

	/**
	 * 获取对象，
	 * 如果竞争不大，直接从threadlocl获取，避免遍历list cas读的消耗；
	 * 否则遍历list cas写，写成功说明抢占对象成功；否则挂起一段时间等待释放
	 * @param timeout
	 * @param timeUnit
	 * @return
	 * @throws InterruptedException
	 */
	public abstract T borrow(long timeout, TimeUnit timeUnit)
			throws InterruptedException;

	/**
	 * 将对象还回
	 *
	 */
	public abstract void requite(T holder);

	/**
	 * 添加新的对象
	 *
	 * @param holder  object holder
	 */
	public abstract void add(T holder);

	public abstract boolean tryShrink();

	/**
	 * 替换损坏的对象
	 *
	 */
	public abstract void replaceAndrequit(T holder, V value);

	/**
	 * 获取当前快照
	 *
	 * @param state  STATE_NOT_IN_USE or STATE_IN_USE
	 * @return 
	 */
	public abstract List<T> values(int state);

	/**
	 * 获得等待线程数
	 *
	 * @return 
	 */
	public abstract int getPendingQueue();

	/**
	 * 得到指定状态的对象总数
	 *
	 * @param state 指定状态
	 * @return 指定状态总数
	 */
	public abstract int getCount(int state);

	/**
	 * 池中数量
	 *
	 * @return 池中数量
	 */
	public abstract int size();

	public abstract void dumpState();

}