/*
 * Copyright 2021 spring-boot-extension the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.livk.commons.spi;

import org.springframework.core.ResolvableType;

import java.util.List;

/**
 * The interface Loader.
 *
 * @author livk
 */
public interface Loader {

    /**
     * Load stream.
     *
     * @param <T>     the type parameter
     * @param type    the type
     * @param manager the loader type
     * @return the stream
     */
    static <T> List<T> load(Class<T> type, LoaderManager manager) {
        return manager.loader().load(type);
    }

    /**
     * Load list.
     *
     * @param <T>            the type parameter
     * @param resolvableType the resolvable type
     * @param manager        the manager
     * @return the list
     */
    @SuppressWarnings("unchecked")
    static <T> List<T> load(ResolvableType resolvableType, LoaderManager manager) {
        return manager.loader().load((Class<T>) resolvableType.resolve());
    }

    /**
     * Load stream.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the stream
     */
    <T> List<T> load(Class<T> type);
}
