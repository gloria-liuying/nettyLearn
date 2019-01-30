package redis;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
/**
 * redis单例连接池
 * @author admin
 *
 */
public class RedisPool {

    private static  int TIMEOUT = 1000*30;
    private static  int MAXTOTAL = 1024;
    private static  int MAXIDLE = 100;
    private static  String REDISIP = "bei1";
    private static  int PORT = 6379;
    private static  String PASSWORD ="default";

    static {
        try {
            Properties prop = PropertiesLoaderUtils.loadAllProperties("redis.properties");
            TIMEOUT = Integer.parseInt(prop.getProperty("TIMEOUT","300000"));
            MAXTOTAL = Integer.parseInt(prop.getProperty("MAXTOTAL","1024"));
            MAXIDLE = Integer.parseInt(prop.getProperty("MAXIDLE","100"));
            REDISIP = prop.getProperty("REDISIP","127.0.0.1");
            PORT = Integer.parseInt(prop.getProperty("PORT","6379"));
            PASSWORD = prop.getProperty("PASSWORD","default");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static JedisPool[] pool = new JedisPool[10];

    private  RedisPool() {}

    private static JedisPool getPool(int database) {
        if(database>10) {
            return null;
        }
        if(pool[database] == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAXTOTAL);
            config.setMaxIdle(MAXIDLE);
            config.setMaxWaitMillis(TIMEOUT);
            config.setTestOnBorrow(true);
            pool[database] = new JedisPool(config,REDISIP,PORT,TIMEOUT,PASSWORD,database);
        }
        return pool[database];
    }
    //单例获取redis连接资源
    public static Jedis getResource(int database) {
        if(database>10) {
            return null;
        }
        Jedis jedis = null;
        if(pool[database] == null) {
            synchronized(RedisPool.class) {
                try {
                    if(pool[database] == null) {
                        pool[database] = getPool(database);
                        try {
                            if (pool[database] != null) {
                                jedis = pool[database].getResource();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            jedis = pool[database].getResource();
        }
        return jedis;
    }

}
