package com.justdebugit.fastpool.pool;

/**
 * 
 * @author wanghongfeng
 *
 */
public class FastPoolConfig {
  
  private  static final int DEFAULT_IDLE_SIZE    = 0;
  private  static final Long DEFAULT_MAX_WAIT_MS = 10000L; //最大等待时间10秒
  private  static final int DEFAULT_MAX_SIZE     = Math.max(Runtime.getRuntime().availableProcessors() * 2, 15);
  
  private static  final long DEFAULT_BETWEEN_TIME = 10000L;
  
  
  private  int minIdle = DEFAULT_IDLE_SIZE;
  private  int maxSize = DEFAULT_MAX_SIZE;
  
  private  Long timeBetweenEvict = DEFAULT_BETWEEN_TIME;
  
  private  Long minEvictableIdle = Long.MAX_VALUE;
  
  
  private  Long maxWaitMs = DEFAULT_MAX_WAIT_MS; //最大等待时间

  
  private  String poolName;
  
  private  boolean disableEvict;
  
  
  public FastPoolConfig(){
    
  }

  public FastPoolConfig(FastPoolConfig config){
    this.minIdle = config.getMinIdle();
    this.maxSize = config.getMaxSize();
    this.timeBetweenEvict = config.getTimeBetweenEvict();
    this.minEvictableIdle = config.getMinEvictableIdle();
    this.maxWaitMs = config.getMaxWaitMs();
    this.poolName = config.getPoolName();
    this.disableEvict = config.isDisableEvict();
  }
  
  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public Long getTimeBetweenEvict() {
    return timeBetweenEvict;
  }


  public Long getMinEvictableIdle() {
    return minEvictableIdle;
  }

  public void setMinEvictableIdle(Long minEvictableIdle) {
    this.minEvictableIdle = minEvictableIdle;
  }

  public Long getMaxWaitMs() {
    return maxWaitMs;
  }

  public void setMaxWaitMs(Long maxWaitMs) {
    this.maxWaitMs = maxWaitMs;
  }

  public int getMinIdle() {
    return minIdle;
  }

  public void setMinIdle(int minIdle) {
    this.minIdle = minIdle;
  }

  public void setTimeBetweenEvict(Long timeBetweenEvict) {
    this.timeBetweenEvict = timeBetweenEvict;
  }

  public String getPoolName() {
    return poolName;
  }

  public void setPoolName(String poolName) {
    this.poolName = poolName;
  }


  public boolean isDisableEvict() {
    return disableEvict;
  }

  public void setDisableEvict(boolean disableEvict) {
    this.disableEvict = disableEvict;
  }

  
  

}
