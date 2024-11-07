package com.crypto_trader.api_server.config.redis;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "spring.data.redis")
@Getter
@Setter
public class RedisProperties {

    private RedisNode master;
    private List<RedisNode> slaves;
    private List<RedisNode> sentinels;

    public RedisProperties() {
        this.master = new RedisNode("mymaster", 6379);
        this.slaves = new ArrayList<>();
        this.sentinels = new ArrayList<>();
    }

    public RedisProperties(RedisNode master, List<RedisNode> sentinels) {
        this.master = master;
        this.slaves = new ArrayList<>();
        this.sentinels = sentinels;
    }

    public RedisProperties(RedisNode master, List<RedisNode> slaves, List<RedisNode> sentinels) {
        this.master = master;
        this.slaves = slaves;
        this.sentinels = sentinels;
    }

    @Getter
    @Setter
    public static class RedisNode {
        private String host;
        private int port;
        private String name;

        public RedisNode() {}

        public RedisNode(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public RedisNode(String host, int port, String name) {
            this.host = host;
            this.port = port;
            this.name = name;
        }
    }
}