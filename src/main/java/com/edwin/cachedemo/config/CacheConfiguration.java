package com.edwin.cachedemo.config;

import com.edwin.cachedemo.cache.DemoCacheManager;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
    @Primary
    public RedisTemplate<String, Object> restTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());


        //JSON序列化配置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);


        //     fastjson
//        ParserConfig.getGlobalInstance().setSafeMode(false);
//        FastJsonRedisSerializer<?> serializer = new FastJsonRedisSerializer<>(Object.class);
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(serializer);
//        template.afterPropertiesSet();

        //     JsonRedisSerializer
//        template.setValueSerializer(new JsonRedisSerializer());
//        template.setHashKeySerializer(new JsonRedisSerializer());

        return template;

    }


    @Bean
    public DemoCacheManager demoCacheManager(RedisConnectionFactory redisConnectionFactory,
                                             RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = createRedisCacheManager(redisConnectionFactory, redisTemplate);
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


    private RedisCacheManager createRedisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                                      RedisTemplate redisTemplate) {

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(redisTemplate.getValueSerializer()))
                .entryTtl(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> initCacheConfiguration = new HashMap<>();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(initCacheConfiguration)
                .build();

    }


}
