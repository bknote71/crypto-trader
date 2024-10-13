package com.crypto_trader.api_server.config.redis;

import io.lettuce.core.models.role.RedisNodeDescription;
import org.springframework.beans.factory.annotation.Value;
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


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    // Default Profile - 단일 Redis 호스트 설정
    @Profile("default")
    @Bean
    public LettuceConnectionFactory redisConnectionFactoryDefault(@Value("${spring.data.redis.host}") String host,
                                                                  @Value("${spring.data.redis.port}") int port) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(config);
    }

    // Prod Profile - 마스터/슬레이브 구조 설정 및 REPLICA_PREFERRED 읽기 우선 처리
    @Profile("prod")
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

    @Bean(name = "pubSubRedisTemplate")
    public ReactiveRedisTemplate<String, String> pubSubReactiveRedisTemplate() {
        String localhost = "localhost";
        String redismaster = "redis-master";
        String host = "3.104.64.244";
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redismaster, 6379);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet(); // 빈 초기화

        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
                .<String, String>newSerializationContext(RedisSerializer.string())
                .value(RedisSerializer.string())
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean(name = "pubSubBytesRedisTemplate")
    @Profile(value = "prod")
    public ReactiveRedisTemplate<String, byte[]> pubSubBytesRedisTemplate() {
        String localhost = "localhost";
        String redismaster = "redis-master";
        String host = "3.104.64.244";
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redismaster, 6379);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet(); // 빈 초기화

        ByteArrayRedisSerializer byteArraySerializer = new ByteArrayRedisSerializer();

        RedisSerializationContext<String, byte[]> serializationContext = RedisSerializationContext
                .<String, byte[]>newSerializationContext(RedisSerializer.string())
                .value(byteArraySerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}