package com.crypto_trader.api_server.config.redis;

import io.lettuce.core.RedisURI;
import io.lettuce.core.models.role.RedisNodeDescription;

import java.time.Duration;

public class RedisNodeDescriptionImpl implements RedisNodeDescription {

    private final String host;
    private final int port;
    private final Role role;

    // 생성자를 통해 호스트, 포트, 역할(Role) 설정
    public RedisNodeDescriptionImpl(String host, int port, Role role) {
        this.host = host;
        this.port = port;
        this.role = role;
    }

    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public RedisURI getUri() {
        return new RedisURI(host, port, Duration.ofSeconds(60));
    }
}
