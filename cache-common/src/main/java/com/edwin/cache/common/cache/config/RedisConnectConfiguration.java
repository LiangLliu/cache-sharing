package com.edwin.cache.common.cache.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisConnectConfiguration implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    private static boolean TAY_REDIS_CONNECT = true;

    public static boolean isTayRedisConnect() {
        return TAY_REDIS_CONNECT;
    }

    private static void endTayRedisConnect() {
        TAY_REDIS_CONNECT = false;
    }


    @Override
    public void run(String... args) {
        try {
            redisTemplate.opsForValue().set("test", "run", 1, TimeUnit.MINUTES);
        } catch (Exception exception) {
            log.error("connect redis error ", exception);
        }
        endTayRedisConnect();
    }
}
