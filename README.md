# fastpool
  高性能线程安全对象池，可以做对象池、同样连接池;
#quick start
 
 ```
 git clone https://github.com/wyzssw/fastpool.git
 cd fastpool
 mvn clean package
 ```
 将打好的包添加到classpath即可
 或者将其发布到nexus
 
 ```
 cd fastpool
 mvn deploy
 ```
 
 然后添加如下依赖到你的pom.xml下
 
 ```xml
       <dependency>
           <groupId>com.justdebugit</groupId>
           <artifactId>fastpool</artifactId>
           <version>1.0-SNAPSHOT</version>
       </dependency>
 ```
 
 
##example

 ```java
 package com.justdebugit.fastpool.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import redis.clients.jedis.Jedis;
import com.justdebugit.fastpool.DefaultFastPool;
import com.justdebugit.fastpool.DefaultFastPool.ObjectFactory;
/**
 * 
 * @author justdebugit
 *
 */
public class FastPoolJedisExample {
	static final Pool<Jedis> fastPool = new DefaultFastPool<Jedis>(5,new ObjectFactory<Jedis>() {
		@Override
		public Jedis makeObject() {
			   return new Jedis("127.0.0.1", 6379);
		}
	});
	
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		int loopCount = 1000;
		for (int i = 0; i < loopCount; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					Jedis jedis = null;
					boolean broken = false;
				    try {
						jedis = fastPool.get();
						System.out.println(jedis.get("key1"));
					} catch (Exception e) {
						e.printStackTrace();
						broken = true;
					}finally{
						if (jedis!=null) {
							fastPool.release(jedis,broken);
						}
					}
				}
			});
		}
	}
}
```
 
