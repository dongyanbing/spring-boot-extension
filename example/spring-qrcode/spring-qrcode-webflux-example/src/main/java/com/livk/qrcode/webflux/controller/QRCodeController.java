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

package com.livk.qrcode.webflux.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.livk.autoconfigure.qrcode.annotation.QRCode;
import com.livk.commons.jackson.util.JsonMapperUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * <p>
 * QRCodeController
 * </p>
 *
 * @author livk
 */
@RestController
@RequestMapping("qrcode")
public class QRCodeController {

    @QRCode
    @GetMapping
    public String text(String text) {
        return text;
    }

    @QRCode
    @GetMapping("mono")
    public Mono<String> textMono(String text) {
        return Mono.just(text);
    }

    @QRCode
    @PostMapping("json")
    public Map<String, String> json(@RequestBody JsonNode node) {
        return JsonMapperUtils.convertValueMap(node, String.class, String.class);
    }

    @QRCode
    @PostMapping("/json/mono")
    public Mono<Map<String, String>> jsonMono(@RequestBody JsonNode node) {
        return Mono.just(JsonMapperUtils.convertValueMap(node, String.class, String.class));
    }
}
