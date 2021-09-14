/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.edwin.cache.common.cache.jdk;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Java Serialization Redis serializer. Delegates to the default (Java based) {@link DefaultSerializer serializer} and
 * {@link DefaultDeserializer}. This {@link RedisSerializer serializer} can be constructed with either custom
 * {@link ClassLoader} or own {@link Converter converters}.
 *
 * @author Mark Pollack
 * @author Costin Leau
 * @author Mark Paluch
 * @author Christoph Strobl
 */
public class DemoJdkSerializationRedisSerializer implements RedisSerializer<Object> {

	private final Converter<Object, byte[]> serializer;
	private final Converter<byte[], Object> deserializer;

	public DemoJdkSerializationRedisSerializer() {
		this(new SerializingConverter(), new DemoDeserializingConverter());
	}

	public DemoJdkSerializationRedisSerializer(@Nullable ClassLoader classLoader) {
		this(new SerializingConverter(), new DemoDeserializingConverter(classLoader));
	}

	public DemoJdkSerializationRedisSerializer(Converter<Object, byte[]> serializer, Converter<byte[], Object> deserializer) {

		Assert.notNull(serializer, "Serializer must not be null!");
		Assert.notNull(deserializer, "Deserializer must not be null!");

		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	public Object deserialize(@Nullable byte[] bytes) {

		if (ObjectUtils.isEmpty(bytes)) {
			return null;
		}

		try {
			return deserializer.convert(bytes);
		} catch (Exception ex) {
			throw new SerializationException("Cannot deserialize", ex);
		}
	}

	@Override
	public byte[] serialize(@Nullable Object object) {
		if (object == null) {
			return new byte[0];
		}
		try {
			return serializer.convert(object);
		} catch (Exception ex) {
			throw new SerializationException("Cannot serialize", ex);
		}
	}
}
