/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

public class DemoObjectInputStream extends ConfigurableObjectInputStream {

    private final ClassLoader classLoader;

    private final boolean acceptProxyClasses;


    public DemoObjectInputStream(InputStream in, @Nullable ClassLoader classLoader) throws IOException {
        this(in, classLoader, true);
    }

    public DemoObjectInputStream(
            InputStream in, @Nullable ClassLoader classLoader, boolean acceptProxyClasses) throws IOException {

        super(in, classLoader, acceptProxyClasses);
        this.classLoader = classLoader;
        this.acceptProxyClasses = acceptProxyClasses;
    }


    @Override
    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {

        String name = classDesc.getName();
        System.out.println(name);

        Map<String, String> map = new HashMap<>();
        map.put("com.edwin.cache.account.User", "com.edwin.cache.user.User");
        map.put("com.edwin.cache.user.User", "com.edwin.cache.account.User");

        if (!ClassUtils.isPresent(name, classLoader)) {
            String className = map.get(name);
            if (!ObjectUtils.isEmpty(className) && ClassUtils.isPresent(className, classLoader)) {
                return ClassUtils.forName(className, this.classLoader);
            }
        }

        return super.resolveClass(classDesc);
    }
}
