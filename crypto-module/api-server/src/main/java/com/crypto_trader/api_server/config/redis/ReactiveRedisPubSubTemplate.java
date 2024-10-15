package com.crypto_trader.api_server.config.redis;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReactiveRedisPubSubTemplate<V> {
    private List<ReactiveRedisTemplate<String, V>> redisTemplates;

    public ReactiveRedisPubSubTemplate(RedisProperties properties, RedisSerializer<V> valueSerializer) {
        this.redisTemplates = new ArrayList<>();

        RedisSerializationContext<String, V> serializationContext = RedisSerializationContext
                .<String, V>newSerializationContext(RedisSerializer.string())
                .value(valueSerializer)
                .build();

        RedisProperties.RedisNode master = properties.getMaster();
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(master.getHost(), master.getPort());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet(); // 빈 초기화

        this.redisTemplates.add(new ReactiveRedisTemplate<>(factory, serializationContext));

        for (RedisProperties.RedisNode slave : properties.getSlaves()) {
            RedisStandaloneConfiguration slaveConfig = new RedisStandaloneConfiguration(slave.getHost(), slave.getPort());
            LettuceConnectionFactory slaveFactory = new LettuceConnectionFactory(slaveConfig);
            slaveFactory.afterPropertiesSet(); // 빈 초기화

            this.redisTemplates.add(new ReactiveRedisTemplate<>(slaveFactory, serializationContext));
        }


    }

    public ReactiveRedisTemplate<String, V> select() {
        return redisTemplates.get(ThreadLocalRandom.current().nextInt(redisTemplates.size()));
    }
}
