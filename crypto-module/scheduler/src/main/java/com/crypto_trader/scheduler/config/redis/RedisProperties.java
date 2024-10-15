package com.crypto_trader.scheduler.config.redis;


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

    public RedisProperties() {
        this.master = new RedisNode("localhost", 6379);
        this.slaves = new ArrayList<>();
    }

    public RedisProperties(RedisNode master, List<RedisNode> slaves) {
        this.master = master;
        this.slaves = slaves;
    }

    public static class RedisNode {
        private String host;
        private int port;

        public RedisNode() {}

        public RedisNode(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}