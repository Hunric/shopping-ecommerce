package com.hunric.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Component
public class RedisUtil {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 存储字符串
     * 
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    /**
     * 存储字符串并设置过期时间
     * 
     * @param key 键
     * @param value 值
     * @param seconds 过期时间（秒）
     */
    public void setEx(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }
    
    /**
     * 获取字符串
     * 
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 删除键
     * 
     * @param key 键
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    /**
     * 检查键是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 设置过期时间
     * 
     * @param key 键
     * @param seconds 过期时间（秒）
     * @return 是否成功
     */
    public boolean expire(String key, long seconds) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, seconds, TimeUnit.SECONDS));
    }
    
    /**
     * 获取过期时间
     * 
     * @param key 键
     * @return 过期时间（秒），-2表示键不存在，-1表示永不过期
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
} 