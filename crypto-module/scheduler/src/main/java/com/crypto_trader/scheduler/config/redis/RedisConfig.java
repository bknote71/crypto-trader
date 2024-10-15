package com.crypto_trader.scheduler.config.redis;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    // TODO: replace
//    @Bean
//    public ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//        ByteArrayRedisSerializer byteArraySerializer = new ByteArrayRedisSerializer();
//
//        RedisSerializationContext<String, byte[]> serializationContext = RedisSerializationContext
//                .<String, byte[]>newSerializationContext(stringSerializer)
//                .value(byteArraySerializer)
//                .build();
//
//        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
//    }

    @Bean
    public ReactiveRedisPubSubTemplate<String> reactiveRedisStringPubSubTemplate(RedisProperties redisProperties) {
        return new ReactiveRedisPubSubTemplate<>(redisProperties, RedisSerializer.string());
    }

    @Bean
    public ReactiveRedisPubSubTemplate<byte[]> reactiveRedisBytesPubSubTemplate(RedisProperties redisProperties) {
        return new ReactiveRedisPubSubTemplate<>(redisProperties, new ByteArrayRedisSerializer());
    }
}
