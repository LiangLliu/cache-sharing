package com.edwin.cache.manager;

import com.edwin.cache.config.RedisConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.Objects;

public class DemoCacheManager implements CacheManager {

    private final RedisCacheManager redisCacheManager;
    private final CaffeineCacheManager caffeineCacheManager;

    public DemoCacheManager(RedisCacheManager redisCacheManager, CaffeineCacheManager caffeineCacheManager) {
        this.redisCacheManager = redisCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
    }


    @Override
    public Cache getCache(String name) {

        if (RedisConfiguration.isRedisActive()) {
            return redisCacheManager.getCache(name);
        }
        return caffeineCacheManager.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        if (RedisConfiguration.isRedisActive()) {
            return redisCacheManager.getCacheNames();
        }
        return caffeineCacheManager.getCacheNames();
    }

    public synchronized void stopRedisCacheMangerForCleanCache() {
        caffeineCacheManager.getCacheNames()
                .forEach(name -> Objects.requireNonNull(redisCacheManager.getCache(name)).clear());
    }
}
