package com.crypto_trader.scheduler.config.redis;


import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.crypto_trader.scheduler.global.constant.RedisConst.REDIS_TICKER;

public class ReactiveRedisPubSubTemplate<V> {
    public final ReactiveRedisTemplate<String, V> master;
    private final List<ReactiveRedisTemplate<String, V>> redisTemplates;

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

        this.master = new ReactiveRedisTemplate<>(factory, serializationContext);
        this.redisTemplates.add(this.master);

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

    public void publish(String key, V value) {
        redisTemplates.forEach(redisTemplate -> redisTemplate.convertAndSend(key, value).subscribe());
    }
}

