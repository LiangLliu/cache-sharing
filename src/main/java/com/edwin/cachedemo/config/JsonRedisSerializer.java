package com.edwin.cachedemo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

public class JsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper om;

    public JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
////        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        this.om = objectMapper;
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        try {
            return om.writeValueAsString(t).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {

        if (bytes == null) {
            return null;
        }

        try {
            return om.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }
}
