/*
 * Copyright 2002-2015 the original author or authors.
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
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;

/**
 * A {@link Converter} that delegates to a
 * {@link Deserializer}
 * to convert data in a byte array to an object.
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0.5
 */
public class DemoDeserializingConverter implements Converter<byte[], Object> {

    private final Deserializer<Object> deserializer;

    public DemoDeserializingConverter() {
        this.deserializer = new DemoDefaultDeserializer();
    }

    public DemoDeserializingConverter(ClassLoader classLoader) {
        this.deserializer = new DemoDefaultDeserializer(classLoader);
    }

    public DemoDeserializingConverter(Deserializer<Object> deserializer) {
        Assert.notNull(deserializer, "Deserializer must not be null");
        this.deserializer = deserializer;
    }

    @Override
    public Object convert(byte[] source) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(source);
        try {
            return this.deserializer.deserialize(byteStream);
        } catch (Throwable ex) {
            throw new SerializationFailedException("Failed to deserialize payload. " +
                    "Is the byte array a result of corresponding serialization for " +
                    this.deserializer.getClass().getSimpleName() + "?", ex);
        }
    }

}
