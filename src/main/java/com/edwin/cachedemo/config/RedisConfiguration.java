package com.edwin.cachedemo.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.edwin.cachedemo.cache.DemoCacheManager;
import io.lettuce.core.event.connection.ConnectionActivatedEvent;
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Bean
    public RedisTemplate<String, Object> restTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());


        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);

        ParserConfig.getGlobalInstance().setSafeMode(false);
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        ParserConfig.getGlobalInstance().addAccept("com.xxx.xxx.xxx.");
        ParserConfig.getGlobalInstance().addAccept("com.xxx.xxx.xxx.");

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setDefaultSerializer(serializer);

        return template;
    }


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

class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Class<T> clazz;

    /**
     * 添加autotype白名单
     * 解决redis反序列化对象时报错 ：com.alibaba.fastjson.JSONException: autoType is not support
     */
//    static {
//        ParserConfig.getGlobalInstance().addAccept("com.***.User");
//    }
    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (null == t) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName)
                .getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (null == bytes || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }

}