package com.edwin.cachedemo.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.edwin.cachedemo.cache.DemoCacheManager;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.lettuce.core.event.connection.ConnectionActivatedEvent;
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfiguration {
    private static boolean REDIS_ACTIVE = false;

    public static boolean isRedisActive() {
        return REDIS_ACTIVE;
    }

    public static void setRedisActive(boolean redisActive) {
        REDIS_ACTIVE = redisActive;
    }


    private final LettuceConnectionFactory lettuceConnectionFactory;


    @Lazy
    private final DemoCacheManager demoCacheManager;


    @PostConstruct
    public void registerRedisLister() {
        lettuceConnectionFactory
                .getClientConfiguration()
                .getClientResources()
                .ifPresent(resources ->
                        resources.eventBus()
                                .get()
                                .subscribe(event -> {
                                    if (event instanceof ConnectionDeactivatedEvent) {
                                        if (RedisConnectConfiguration.isTayRedisConnect()) {
                                            return;
                                        }
                                        setRedisActive(false);
                                        demoCacheManager.stopRedisCacheMangerForCleanCache();
                                    } else if (event instanceof ConnectionActivatedEvent) {
                                        if (RedisConnectConfiguration.isTayRedisConnect()) {
                                            setRedisActive(true);
                                            return;
                                        }
                                        setRedisActive(true);
                                    } else {
                                        log.info("redis event: " + event.getClass());
                                    }
                                })

                );
    }

}




