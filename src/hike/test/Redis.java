package hike.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Redis {
	private static Redis _instance;
	private JedisPool redisPool;
	
	private Redis()
    {
    	JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setMaxIdle(100);
        redisPool = new JedisPool(poolConfig, "localhost");
    }
	
	public static Redis getInstance()
	{
		if(_instance == null)
		{
			synchronized(Redis.class)
			{
				if(_instance == null)
					_instance = new Redis();
			}
		}
		return _instance;
	}
	
	public void put(String key, String value,int TTL){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.set( key,value);
        	if(TTL!=0)
        		jedis.expire(key, TTL);
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }
    	
    }
    
	public String get(String key){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
        	return jedis.get(key);
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public void hput(String hset,String key, String value){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.hset(hset, key, value);
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }
    }
    
	public String hget(String hset,String key){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();

        	return jedis.hget(hset, key);
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }
    }
    
	public List<String> hmget(String hset,String... keys){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
        	return jedis.hmget(hset, keys);
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }
    }
    
	public Map<String, String> hgetAll(String hset){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
        	return jedis.hgetAll(hset);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }
    }
    
	public Set<String> hgetKeys(String hset){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
        	return jedis.hkeys(hset);
            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }
    }
    
	public void del(String key){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.del(key);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public void hdel(String hset,String hkey){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.hdel(hset,hkey);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public boolean exists(String key){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.exists(key);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return false; //TODO check if this will create any problems
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public boolean hexists(String hset,String hkey){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.hexists(hset,hkey);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return false; //TODO check if this will create any problems
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public void hincrBy(String key,String field,int value){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.hincrBy(key,field,value);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public void sadd(String key,String... members){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.sadd(key, members);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public void srem(String key,String... members){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.srem(key,members);   
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public Set<String> smembers(String key){ // SLOW!!! Should be used only when number of members is small.
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.smembers(key);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public boolean sismember(String key,String mem){ // SLOW!!! Should be used only when number of members is small.
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.sismember(key,mem);            
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return false;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
    
	public Object eval(String script, int keyCount, String... params){
    	Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.eval(script, keyCount, params);  
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
    }
	
	public void rpush(String key,String... values){
		Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.rpush(key, values);  
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
	}
	
	public void lpush(String key,String... values){
		Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            jedis.lpush(key, values);  
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
	}
	
	public String lpop(String key){
		Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.lpop(key);  
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
	}
	
	public Long llen(String key){
		Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.llen(key);  
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
	}
	
	public List<String> lrange(String key,long start,long end){
		Jedis jedis = null;
        try
        {
            jedis = redisPool.getResource();
            return jedis.lrange(key, start, end);  
        }
        catch (JedisConnectionException e) 
        {
            if (jedis != null) 
            {
                redisPool.returnBrokenResource(jedis);
                jedis = null;
            }
            return null;
        }
        finally
        {
            if (jedis != null)
            {
                redisPool.returnResource(jedis);
            }
        }	
	}
}
