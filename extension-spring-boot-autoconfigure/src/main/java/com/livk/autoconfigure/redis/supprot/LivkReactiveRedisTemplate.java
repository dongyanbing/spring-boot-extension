package com.livk.autoconfigure.redis.supprot;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * <p>
 * LivkReactiveRedisTemplate
 * </p>
 *
 * @author livk
 */
public class LivkReactiveRedisTemplate extends ReactiveRedisTemplate<String, Object> {

    /**
     * Instantiates a new Livk reactive redis template.
     *
     * @param connectionFactory    the connection factory
     * @param serializationContext the serialization context
     */
    public LivkReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory,
                                     RedisSerializationContext<String, Object> serializationContext) {
        this(connectionFactory, serializationContext, false);
    }

    /**
     * Instantiates a new Livk reactive redis template.
     *
     * @param connectionFactory    the connection factory
     * @param serializationContext the serialization context
     * @param exposeConnection     the expose connection
     */
    public LivkReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory,
                                     RedisSerializationContext<String, Object> serializationContext, boolean exposeConnection) {
        super(connectionFactory, serializationContext, exposeConnection);
    }

}
