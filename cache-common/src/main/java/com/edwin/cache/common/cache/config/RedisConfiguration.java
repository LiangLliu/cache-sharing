package com.edwin.cache.common.cache.config;

import com.edwin.cache.common.cache.manager.DemoCacheManager;
import io.lettuce.core.event.connection.ConnectionActivatedEvent;
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import javax.annotation.PostConstruct;

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




