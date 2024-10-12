package com.crypto_trader.api_server.config.redis;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.models.role.RedisNodeDescription;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LBReadFrom extends ReadFrom {

    private final RedisNodeDescription masterNode;
    private final List<RedisNodeDescription> replicas;

    public LBReadFrom(RedisNodeDescription masterNode,
                      List<RedisNodeDescription> replicaNodes) {
        this.masterNode = masterNode;
        this.replicas = replicaNodes;
    }

    @Override
    public List<RedisNodeDescription> select(Nodes nodes) {
        if (replicas.isEmpty()) {
            return Collections.singletonList(masterNode);
        }

        RedisNodeDescription description = replicas.get(ThreadLocalRandom.current().nextInt(replicas.size()));
        return Collections.singletonList(description);
    }
}