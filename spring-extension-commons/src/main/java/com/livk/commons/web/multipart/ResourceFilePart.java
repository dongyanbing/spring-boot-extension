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

package com.livk.commons.web.multipart;

import com.livk.commons.io.DataBufferUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

/**
 * The type Resource file part.
 *
 * @author livk
 */
@RequiredArgsConstructor
public class ResourceFilePart implements FilePart {

    private final HttpHeaders headers;

    private final Resource resource;

    /**
     * Instantiates a new Resource file part.
     *
     * @param resource the resource
     */
    public ResourceFilePart(Resource resource) {
        this(new HttpHeaders(), resource);
    }

    @Override
    public String filename() {
        String name = headers().getContentDisposition().getName();
        return StringUtils.hasText(name) ? name : resource.getFilename();
    }

    @Override
    public Mono<Void> transferTo(Path dest) {
        return blockingOperation(() ->
                Files.copy(resource.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING));
    }

    private Mono<Void> blockingOperation(Callable<?> callable) {
        return Mono.<Void>create(sink -> {
            try {
                callable.call();
                sink.success();
            } catch (Exception ex) {
                sink.error(ex);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public String name() {
        return filename();
    }

    @Override
    public HttpHeaders headers() {
        return headers;
    }

    @Override
    public Flux<DataBuffer> content() {
        return DataBufferUtils.read(resource, DataBufferUtils.DEFAULT_FACTORY, DataBufferUtils.BUFFER_SIZE)
                .publishOn(Schedulers.boundedElastic());
    }
}
