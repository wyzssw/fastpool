package com.justdebugit.fastpool.pool;



import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 高性能对象池
 *
 *
 * @param <V> ultimately value store in entry
 * @param <T> value entry
 */
class FastPool<T extends IEntry> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FastPool.class);
  private final AbstractQueuedLongSynchronizer synchronizer;
  private final CopyOnWriteArrayList<IEntry> entryList;// entry pool
  private final AtomicLong sequence;
  private final PoolListener listener;
  private final AtomicInteger waiters;
  
  private volatile boolean close;

  private final ThreadLocal<ArrayList<WeakReference<IEntry>>> threadList;

  public FastPool(PoolListener listener) {
    this.listener = listener;
    this.entryList = new CopyOnWriteArrayList<IEntry>();
    this.synchronizer = new Synchronizer();
    this.sequence = new AtomicLong(1);// sequence并非代表池中数量，而是使用次数，只增不减
    this.threadList = new ThreadLocal<ArrayList<WeakReference<IEntry>>>();
    this.waiters = new AtomicInteger();
  }

  
  public interface PoolListener
  {
     Future<Boolean> addEntry();
  }

  /**
   * 获取对象， 如果竞争不大，直接从threadlocl获取，避免遍历list cas读的消耗； 否则遍历list cas写，写成功说明抢占对象成功；否则挂起一段时间等待释放
   * 
   * @param timeout
   * @param timeUnit
   * @return
   * @throws InterruptedException
   */
  @SuppressWarnings("unchecked")
  public T borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException {
    if (!synchronizer.hasQueuedThreads()) {
      final ArrayList<WeakReference<IEntry>> list = threadList.get();
      if (list == null) {
        threadList.set(new ArrayList<WeakReference<IEntry>>(16));
      } else {
        for (int i = list.size() - 1; i >= 0; i--) {
          final IEntry entry = list.remove(i).get();
          if (entry != null
              && entry.compareAndSet(States.STATE_NOT_IN_USE, States.STATE_IN_USE)) {
            return (T) entry;
          }
        }
      }
    }
    // 否则扫描列表
    timeout = timeUnit.toNanos(timeout);
    final long startScan = System.nanoTime();
    final long originTimeout = timeout;
    Future<Boolean> addItemFuture = null;
    waiters.incrementAndGet();
    try {
      do {
        long startSeq;
        do {
          startSeq = sequence.get();
          for (final IEntry entry : entryList) {
            if (entry.compareAndSet(States.STATE_NOT_IN_USE, States.STATE_IN_USE)) {
              if (waiters.get() > 1 && addItemFuture == null) {//把其它线程创建的entry抢走了
                listener.addEntry();
             }
             return (T) entry;
            }
          }
        } while (startSeq < sequence.get());// 有还回就重试；可以不经历入队列park的过程提高并发能力
        if (addItemFuture == null || addItemFuture.isDone()) {
          addItemFuture = listener.addEntry();
        }
        // seq只增不减，tryAcquireShared仅仅判断距离startSeq赋值state有无变化，即有没有对象还回发生
        if (!synchronizer.tryAcquireSharedNanos(startSeq, timeout)) {
          return null;
        }
        final long elapsed = (System.nanoTime() - startScan);
        timeout = originTimeout - Math.max(elapsed, 100L);
      } while (timeout > 1000L); // 1000ns
    } finally {
      waiters.decrementAndGet();
    }
    return null;
  }

  /**
   * 将对象还回
   *
   */
  public void requite(final T entry) {
    if (entry.compareAndSet(States.STATE_IN_USE, States.STATE_NOT_IN_USE)) {
      final ArrayList<WeakReference<IEntry>> list = threadList.get();
      if (list != null) {
        list.add(new WeakReference<IEntry>(entry));
      }
      synchronizer.releaseShared(sequence.incrementAndGet());
    } else {
      LOGGER.warn("pool execute an object leak", entry.toString());
    }
  }

  /**
   * 添加新的对象
   *
   * @param entry object entry
   */
  public void add(final T entry) {
    if (close) {
       LOGGER.info("FastPool has been closed, ignoring add ");
       throw new IllegalStateException("FastPool has been closed, ignoring add");
    }
    entryList.add(entry);
    synchronizer.releaseShared(sequence.incrementAndGet());
  }



  /**
   * 
   * 只在return broken时或者reserve后调用
   * @param entry
   * @return
   */
  public boolean remove(T entry) {
    if (!entry.compareAndSet(States.STATE_IN_USE, States.STATE_REMOVED) && !entry.compareAndSet(States.STATE_RESERVED, States.STATE_REMOVED) && !close) {
      LOGGER.error("Attempt to remove an object error: {}", entry.toString());
      throw new IllegalStateException("pool execute an object leak");
    }
    final boolean removed = entryList.remove(entry);
    return removed;
  }


  /**
   * 只针对NOT_IN_USE,reserve后可以进行check等操作
   * @param entry
   * @return
   */
  public boolean reserve(T entry) {
    return entry.compareAndSet(States.STATE_NOT_IN_USE, States.STATE_RESERVED);
  }


  /**
   * 获取当前快照
   *
   * @param state STATE_NOT_IN_USE or STATE_IN_USE
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<T> values(final States state) {
    final ArrayList<T> list = new ArrayList<T>(entryList.size());
    if (state == States.STATE_IN_USE || state == States.STATE_NOT_IN_USE) {
      for (final IEntry reference : entryList) {
        if (reference.state() == state) {
          list.add((T) reference);
        }
      }
    }
    return list;
  }

  public List<T> values() {
    ArrayList<T> list = new ArrayList<T>(entryList.size());
    return list;
  }


  /**
   * 获得等待线程数
   *
   * @return
   */
  public int getPendingQueue() {
    return synchronizer.getQueueLength();
  }

  /**
   * 得到指定状态的对象总数
   *
   * @param state 指定状态
   * @return 指定状态总数
   */
  public int getCount(final States state) {
    int count = 0;
    for (final IEntry reference : entryList) {
      if (reference.state() == state) {
        count++;
      }
    }
    return count;
  }

  public void close(){
    this.close = true;
  }

  /**
   * 池中数量
   *
   * @return 池中数量
   */
  public int size() {
    return entryList.size();
  }

  public void dumpState() {
    for (IEntry entry : entryList) {
      LOGGER.info(entry.toString());
    }
  }

  public long getSeq() {
    return sequence.get();
  }

  public class Synchronizer extends AbstractQueuedLongSynchronizer {

    private static final long serialVersionUID = 6305735919591847529L;

    /**
     * 简单公平锁的实现，当自己不是头结点的下一个结点时，也即自己未曾在队列排队 返回值大于等于0将代表获取锁成功，返回值小于0代表获取锁失败，需要进行AQS队列
     * getState()-seq代表这段时间变动过，有还回或者添加操作发生；否则就表示没有变动过 getState()-seq-1
     * 为了在传播唤醒后续节点时，在只有1个资源可用的情况下，选择不唤醒后继节点, 防止同时唤醒增加线程间竞争
     */
    @Override
    protected long tryAcquireShared(final long seq) {
      return hasQueuedPredecessors() ? -1L : getState() - seq - 1;
    }


    @Override
    protected boolean tryReleaseShared(final long ignored) {
      setState(sequence.get());
      return true;
    }


  }
}

