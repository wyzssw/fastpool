package com.justdebugit.fastpool.codis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.BasicCommands;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ScriptingCommands;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

/**
 * only codis commmand
 *
 */
public class Codis implements JedisCommands, ScriptingCommands, BasicCommands{
  
  private CodisPool pool;
  
  public CodisPool getPool(){
    return pool;
  }
  
  public  Codis(CodisPool codisPool) {
    this.pool = codisPool;
  }

  @Override
  public String ping() {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.ping();
    } catch (Exception e) {
      broken = true;
      System.out.println(broken);
      throw e;
    } finally {
      System.out.println(broken);
      pool.release(jedis, broken);
    }
  }

  /**
   * codis don't support
   */
  @Override
  @Deprecated
  public String quit() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String flushDB() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public Long dbSize() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String select(int index) {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String flushAll() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String auth(String password) {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String save() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String bgsave() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String bgrewriteaof() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public Long lastsave() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String shutdown() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String info() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String info(String section) {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String slaveof(String host, int port) {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String slaveofNoOne() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public Long getDB() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String debug(DebugParams params) {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public String configResetStat() {
    throw new UnsupportedOperationException();
  }

  /**
   * codis don't support
   */
  @Deprecated
  @Override
  public Long waitReplicas(int replicas, long timeout) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object eval(String script, int keyCount, String... params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.eval(script, keyCount, params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Object eval(String script, List<String> keys, List<String> args) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.eval(script, keys, args);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Object eval(String script) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.eval(script);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Object evalsha(String script) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.evalsha(script);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Object evalsha(String sha1, List<String> keys, List<String> args) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.evalsha(sha1, keys, args);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Object evalsha(String sha1, int keyCount, String... params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.evalsha(sha1, keyCount, params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean scriptExists(String sha1) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.scriptExists(sha1);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<Boolean> scriptExists(String... sha1) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.scriptExists(sha1);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String scriptLoad(String script) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.scriptLoad(script);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String set(String key, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.set(key,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  
  @Override
  public String set(String key, String value, String nxxx, String expx, long time) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.set(key,value,nxxx,expx,time);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String set(String key, String value, String nxxx) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.set(key,value,nxxx);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String get(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.get(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean exists(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.exists(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long persist(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.persist(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String type(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.type(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long expire(String key, int seconds) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.expire(key, seconds);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long pexpire(String key, long milliseconds) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.pexpire(key, milliseconds);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long expireAt(String key, long unixTime) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.expireAt(key, unixTime);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long pexpireAt(String key, long millisecondsTimestamp) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.pexpireAt(key, millisecondsTimestamp);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long ttl(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.ttl(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long pttl(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.pttl(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean setbit(String key, long offset, boolean value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.setbit(key,offset,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean setbit(String key, long offset, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.setbit(key,offset,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean getbit(String key, long offset) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.getbit(key,offset);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long setrange(String key, long offset, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.setrange(key,offset,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String getrange(String key, long startOffset, long endOffset) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.getrange(key,startOffset,endOffset);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String getSet(String key, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.getSet(key,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long setnx(String key, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.setnx(key,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String setex(String key, int seconds, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.setex(key,seconds,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String psetex(String key, long milliseconds, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.psetex(key,milliseconds,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long decrBy(String key, long integer) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.decrBy(key,integer);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long decr(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.decr(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long incrBy(String key, long integer) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.incrBy(key,integer);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double incrByFloat(String key, double value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.incrByFloat(key,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long incr(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.incr(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long append(String key, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.append(key,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String substr(String key, int start, int end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.substr(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long hset(String key, String field, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hset(key,field,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String hget(String key, String field) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hget(key,field);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long hsetnx(String key, String field, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hsetnx(key,field,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String hmset(String key, Map<String, String> hash) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hmset(key,hash);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> hmget(String key, String... fields) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hmget(key,fields);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long hincrBy(String key, String field, long value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hincrBy(key,field,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double hincrByFloat(String key, String field, double value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hincrByFloat(key,field,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean hexists(String key, String field) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hexists(key,field);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long hdel(String key, String... field) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hdel(key,field);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long hlen(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hlen(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> hkeys(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hkeys(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> hvals(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hvals(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Map<String, String> hgetAll(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hgetAll(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long rpush(String key, String... string) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.rpush(key,string);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long lpush(String key, String... string) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lpush(key,string);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long llen(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.llen(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> lrange(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lrange(key, start, end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String ltrim(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.ltrim(key, start, end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String lindex(String key, long index) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lindex(key,index);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String lset(String key, long index, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lset(key,index,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long lrem(String key, long count, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lrem(key,count,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String lpop(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lpop(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String rpop(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.rpop(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long sadd(String key, String... member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sadd(key,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> smembers(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.smembers(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long srem(String key, String... member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.srem(key,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String spop(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.spop(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> spop(String key, long count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.spop(key,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long scard(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.scard(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Boolean sismember(String key, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sismember(key, member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String srandmember(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.srandmember(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> srandmember(String key, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.srandmember(key,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long strlen(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.strlen(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zadd(String key, double score, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zadd(key,score,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zadd(String key, double score, String member, ZAddParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zadd(key, score,member,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zadd(String key, Map<String, Double> scoreMembers) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zadd(key,scoreMembers);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zadd(key,scoreMembers,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrange(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrange(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zrem(String key, String... member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrem(key,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double zincrby(String key, double score, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zincrby(key,score,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double zincrby(String key, double score, String member, ZIncrByParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zincrby(key,score,member,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zrank(String key, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrank(key,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zrevrank(String key, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrank(key,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrange(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrange(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrangeWithScores(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeWithScores(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeWithScores(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zcard(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zcard(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double zscore(String key, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zscore(key,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> sort(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sort(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> sort(String key, SortingParams sortingParameters) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sort(key,sortingParameters);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zcount(String key, double min, double max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zcount(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zcount(String key, String min, String max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zcount(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrangeByScore(String key, double min, double max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScore(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrangeByScore(String key, String min, String max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScore(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrangeByScore(String key, double max, double min) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScore(key,max,min);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScore(key,min,max,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrangeByScore(String key, String max, String min) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScore(key,max,min);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScore(key,min,max,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScore(key,max,min,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScoreWithScores(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScoreWithScores(key,max,min);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset,
      int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScoreWithScores(key,min,max,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScore(key,max,min,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScoreWithScores(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScoreWithScores(key,max,min);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset,
      int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByScoreWithScores(key,min,max,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset,
      int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScoreWithScores(key,max,min,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset,
      int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByScoreWithScores(key,max,min,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zremrangeByRank(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zremrangeByRank(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zremrangeByScore(String key, double start, double end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zremrangeByScore(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zremrangeByScore(String key, String start, String end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zremrangeByScore(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zlexcount(String key, String min, String max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zlexcount(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrangeByLex(String key, String min, String max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByLex(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrangeByLex(key,min,max,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrangeByLex(String key, String max, String min) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByLex(key,max,min);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zrevrangeByLex(key,max,min,offset,count);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long zremrangeByLex(String key, String min, String max) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zremrangeByLex(key,min,max);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.linsert(key,where,pivot,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long lpushx(String key, String... string) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.lpush(key,string);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long rpushx(String key, String... string) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.rpushx(key,string);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> blpop(String arg) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.blpop(arg);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> blpop(int timeout, String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.blpop(timeout,key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> brpop(String arg) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.brpop(arg);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> brpop(int timeout, String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.brpop(timeout,key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long del(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.del(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public String echo(String string) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.echo(string);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Deprecated
  @Override
  public Long move(String key, int dbIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Long bitcount(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.bitcount(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long bitcount(String key, long start, long end) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.bitcount(key,start,end);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long bitpos(String key, boolean value) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.bitpos(key,value);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long bitpos(String key, boolean value, BitPosParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.bitpos(key,value,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hscan(key,cursor);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<String> sscan(String key, int cursor) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sscan(key,cursor);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<Tuple> zscan(String key, int cursor) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zscan(key,cursor);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hscan(key,cursor);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.hscan(key,cursor,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<String> sscan(String key, String cursor) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sscan(key,cursor);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.sscan(key,cursor,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<Tuple> zscan(String key, String cursor) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zscan(key,cursor);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.zscan(key, cursor,params);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long pfadd(String key, String... elements) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.pfadd(key,elements);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public long pfcount(String key) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.pfcount(key);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long geoadd(String key, double longitude, double latitude, String member) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.geoadd(key,longitude,latitude,member);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.geoadd(key,memberCoordinateMap);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double geodist(String key, String member1, String member2) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.geodist(key,member1,member2);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public Double geodist(String key, String member1, String member2, GeoUnit unit) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.geodist(key,member1,member2,unit);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<String> geohash(String key, String... members) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.geohash(key,members);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<GeoCoordinate> geopos(String key, String... members) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.geopos(key,members);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude,
      double radius, GeoUnit unit) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.georadius(key,longitude,latitude,radius,unit);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude,
      double radius, GeoUnit unit, GeoRadiusParam param) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.georadius(key,longitude,latitude,radius,unit,param);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius,
      GeoUnit unit) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.georadiusByMember(key,member,radius,unit);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  @Override
  public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius,
      GeoUnit unit, GeoRadiusParam param) {
    Jedis jedis = pool.getInTime();
    boolean broken = false;
    try {
      return jedis.georadiusByMember(key,member,radius,unit,param);
    } catch (Exception e) {
      broken = true;
      throw e;
    } finally {
      pool.release(jedis, broken);
    }
  }

  
  

}
