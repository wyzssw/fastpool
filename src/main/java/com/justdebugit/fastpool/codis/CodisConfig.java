package com.justdebugit.fastpool.codis;

import com.justdebugit.fastpool.pool.FastPoolConfig;

import redis.clients.jedis.Protocol;

public class CodisConfig {
  
  private FastPoolConfig poolConfig;
  
  private int connectionTimeout =  Protocol.DEFAULT_TIMEOUT;
  private int soTimeout = Protocol.DEFAULT_TIMEOUT;;
  

  public FastPoolConfig getPoolConfig() {
    return poolConfig;
  }

  public void setPoolConfig(FastPoolConfig poolConfig) {
    this.poolConfig = poolConfig;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public int getSoTimeout() {
    return soTimeout;
  }

  public void setSoTimeout(int soTimeout) {
    this.soTimeout = soTimeout;
  }

}
