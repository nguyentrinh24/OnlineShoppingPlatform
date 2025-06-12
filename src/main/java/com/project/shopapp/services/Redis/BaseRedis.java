package com.project.shopapp.services.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BaseRedis {

    private final RedisTemplate<String, Object> redisTemplate;

    //
    @Value("${spring.data.redis.time-to-live}")
    private long defaultTtl;

    @Autowired
    public BaseRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 1. set với TTL
    public void set(String key, Object value) {
        set(key, value, defaultTtl);
    }

    //set với TTL
    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    // get nếu hết TTL sẽ trả null
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    // xóa key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // kiểm tra tồn tại
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // thay đổi TTL
    public boolean expire(String key, long ttlSeconds) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS));
    }

    // lấy TTL còn lại
    public Long getTtl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
