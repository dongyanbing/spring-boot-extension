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

package com.livk.autoconfigure.redisson;

import com.livk.auto.service.annotation.SpringAutoService;
import com.livk.commons.spring.env.SpringEnvBinder;
import com.livk.commons.util.YamlUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.RedisException;
import org.redisson.config.Config;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * RedissonAutoConfiguration
 * </p>
 *
 * @author livk
 */
@SpringAutoService
@ConditionalOnClass(Redisson.class)
@AutoConfiguration(before = RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonAutoConfiguration implements EnvironmentAware {
    private static final String REDISSON_CONFIG = "spring.redisson.config";

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    private Environment environment;

    /**
     * Redisson reactive client.
     *
     * @param redisson the redisson
     * @return the redisson reactive client
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactive(RedissonClient redisson) {
        return redisson.reactive();
    }

    /**
     * Redisson client redisson client.
     *
     * @param redisProperties   the redis properties
     * @param configCustomizers the config customizers
     * @return the redisson client
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient(RedisProperties redisProperties,
                                         ObjectProvider<ConfigCustomizer> configCustomizers) {
        Properties redissonProperties = new SpringEnvBinder(environment).propertiesOf(REDISSON_CONFIG)
                .orElseGet(Properties::new);
        String redissonYaml = YamlUtils.toYml(redissonProperties).replaceAll("'", "");
        Config config;
        Duration duration = redisProperties.getTimeout();
        int timeout = duration == null ? 10000 : (int) duration.toMillis();
        if (StringUtils.hasText(redissonYaml)) {
            try {
                config = Config.fromYAML(redissonYaml);
            } catch (IOException e) {
                throw new RedisException(e);
            }
        } else if (redisProperties.getSentinel() != null) {
            List<String> nodeList = redisProperties.getSentinel().getNodes();
            String[] nodes = convert(nodeList);
            config = new Config();
            config.useSentinelServers()
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .addSentinelAddress(nodes)
                    .setDatabase(redisProperties.getDatabase())
                    .setConnectTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else if (redisProperties.getCluster() != null) {
            List<String> nodeList = redisProperties.getCluster().getNodes();
            String[] nodes = convert(nodeList);
            config = new Config();
            config.useClusterServers()
                    .addNodeAddress(nodes)
                    .setConnectTimeout(timeout)
                    .setPassword(redisProperties.getPassword());
        } else {
            config = new Config();
            String prefix = REDIS_PROTOCOL_PREFIX;
            if (redisProperties.getSsl().isEnabled()) {
                prefix = REDISS_PROTOCOL_PREFIX;
            }
            config.useSingleServer()
                    .setAddress(prefix + redisProperties.getHost() + ":" + redisProperties.getPort())
                    .setConnectTimeout(timeout)
                    .setDatabase(redisProperties.getDatabase())
                    .setPassword(redisProperties.getPassword());
        }
        configCustomizers.orderedStream().forEach(customizer -> customizer.customize(config));
        return Redisson.create(config);
    }

    private String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(REDIS_PROTOCOL_PREFIX + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
