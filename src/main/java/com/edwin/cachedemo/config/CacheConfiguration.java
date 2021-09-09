package com.edwin.cachedemo.config;

import com.edwin.cachedemo.cache.DemoCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {

    @Override
    public CacheErrorHandler errorHandler() {
        return super.errorHandler();
    }

    @Bean
    public DemoCacheManager demoCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = createRedisCacheManager(redisConnectionFactory);
        CaffeineCacheManager caffeineCacheManager = createCaffeineCacheManager();
        return new DemoCacheManager(redisCacheManager, caffeineCacheManager);
    }


    private CaffeineCacheManager createCaffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(100)
                .maximumSize(10000)
        );
        return caffeineCacheManager;
    }


    private RedisCacheManager createRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig().entryTtl(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> initCacheConfiguration = new HashMap<>();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(initCacheConfiguration)
                .build();

    }

}
