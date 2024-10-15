package com.crypto_trader.api_server.config.redis;

import io.lettuce.core.models.role.RedisNodeDescription;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;

import static io.lettuce.core.models.role.RedisInstance.*;

@EnableConfigurationProperties(RedisProperties.class)
@Configuration
@Profile("prod")
public class RedisProdConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactoryProd(RedisProperties redisProperties) {
        String masterHost = redisProperties.getMaster().getHost();
        int masterPort = redisProperties.getMaster().getPort();

        RedisStaticMasterReplicaConfiguration staticMasterReplicaConfig = new RedisStaticMasterReplicaConfiguration(masterHost, masterPort);
        RedisNodeDescription masterNode = new RedisNodeDescriptionImpl(masterHost, masterPort, Role.MASTER);

        List<RedisNodeDescription> slaveNodes = new ArrayList<>();

        for (RedisProperties.RedisNode slave : redisProperties.getSlaves()) {
            RedisNodeDescriptionImpl slaveNode = new RedisNodeDescriptionImpl(slave.getHost(), slave.getPort(), Role.REPLICA);
            slaveNodes.add(slaveNode);
            staticMasterReplicaConfig.addNode(slave.getHost(), slave.getPort());
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(new LBReadFrom(masterNode, slaveNodes))  // 슬레이브에서 읽기 우선 처리
                .build();

        return new LettuceConnectionFactory(staticMasterReplicaConfig, clientConfig);
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
    public ReactiveRedisPubSubTemplate<String> reactiveRedisStringPubSubTemplate(RedisProperties redisProperties) {
        return new ReactiveRedisPubSubTemplate<>(redisProperties, RedisSerializer.string());
    }

    @Bean
    public ReactiveRedisPubSubTemplate<byte[]> reactiveRedisBytesPubSubTemplate(RedisProperties redisProperties) {
        return new ReactiveRedisPubSubTemplate<>(redisProperties, new ByteArrayRedisSerializer());
    }
}

