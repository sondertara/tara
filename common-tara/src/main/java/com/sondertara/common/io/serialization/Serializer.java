/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sondertara.common.io.serialization;


import com.sondertara.common.collection.Maps;
import com.sondertara.common.exception.CodecException;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ConcurrentMap;

/**
 * Serializer for serialize and deserialize.
 *
 * @author jiangping
 * @version $Id: Serializer.java, v 0.1 2015-10-4 PM9:37:57 tao Exp $
 */
public interface Serializer {
    static final ConcurrentMap<String, Class<?>> classCache = Maps.newConcurrentMap();

    /**
     * Encode object into bytes.
     *
     * @param obj target object
     * @return serialized result
     */
    <T> byte[] serialize(final T obj) throws CodecException;

    /**
     * Decode bytes into Object.
     *
     * @param data     serialized data
     * @param classOfT class of original data
     */
    @SuppressWarnings("unchecked")
    default <T> T deserialize(final byte[] data, String classOfT) throws CodecException {
        Class<?> clazz = classCache.get(classOfT);
        if (clazz == null) {
            try {
                final Class<?> newClazz = Class.forName(classOfT);
                clazz = classCache.putIfAbsent(classOfT, newClazz);
                if (clazz == null) {
                    clazz = newClazz;
                }
            } catch (final Exception e) {
                throw new CodecException(e);
            }
        }
        return (T) deserialize(data, clazz);
    }

    /**
     * Decode bytes into Object.
     *
     * @param data     serialized data
     * @param tClass class of original data
     */

    <T> T deserialize(final byte[] data, @Nullable Class<T> tClass) throws CodecException;

    byte id();
}
