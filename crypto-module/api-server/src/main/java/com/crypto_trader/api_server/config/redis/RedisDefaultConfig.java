package com.crypto_trader.api_server.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("default")
public class RedisDefaultConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactoryDefault(@Value("${spring.data.redis.host}") String host,
                                                                  @Value("${spring.data.redis.port}") int port) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }

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

    @Bean
    public ReactiveRedisPubSubTemplate<String> reactiveRedisStringPubSubPool() {
        return new ReactiveRedisPubSubTemplate<>(new RedisProperties(), RedisSerializer.string());
    }

    @Bean
    public ReactiveRedisPubSubTemplate<byte[]> reactiveRedisBytesPubSubPool() {
        return new ReactiveRedisPubSubTemplate<>(new RedisProperties(), new ByteArrayRedisSerializer());
    }
}
