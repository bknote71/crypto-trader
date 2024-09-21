package com.crypto_trader.api_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        ByteArrayRedisSerializer byteArraySerializer = new ByteArrayRedisSerializer();

        RedisSerializationContext<String, byte[]> serializationContext = RedisSerializationContext
                .<String, byte[]>newSerializationContext(stringSerializer)
                .value(byteArraySerializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}