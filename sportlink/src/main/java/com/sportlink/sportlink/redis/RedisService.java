package com.sportlink.sportlink.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Store a key-value pair in Redis with an expiration time.
     */
    public void saveValueWithExpiration(String key, String value, long expirationInMinutes) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(expirationInMinutes));
    }

    /**
     * Retrieve a value from Redis by key.
     */
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Delete a key from Redis.
     */
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Check if a key exists in Redis.
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }


    public String getPasswdResetRecordKey(String loginEmail) {
        return "PASSWD-"+loginEmail;
    }

    public void clearAll() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
