/*
 * Copyright 2002-2017 the original author or authors.
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

import org.springframework.core.NestedIOException;
import org.springframework.core.serializer.Deserializer;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * A default {@link Deserializer} implementation that reads an input stream
 * using Java serialization.
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0.5
 * @see ObjectInputStream
 */
public class DemoDefaultDeserializer implements Deserializer<Object> {

	@Nullable
	private final ClassLoader classLoader;

	public DemoDefaultDeserializer() {
		this.classLoader = null;
	}

	public DemoDefaultDeserializer(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	@SuppressWarnings("resource")
	public Object deserialize(InputStream inputStream) throws IOException {
		ObjectInputStream objectInputStream = new DemoObjectInputStream(inputStream, this.classLoader);
		try {
			return objectInputStream.readObject();
		}
		catch (ClassNotFoundException ex) {
			throw new NestedIOException("Failed to deserialize object type", ex);
		}
	}
}
