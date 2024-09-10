package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.domain.Candle;
import com.crypto_trader.scheduler.domain.CandleState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crypto_trader.scheduler.global.constant.RedisConst.MINUTE_CANDLE;

@Repository
public class SimpleCandleRepository {

    // private state
    private final Map<String, CandleState> candleStates = new ConcurrentHashMap<>();

    public Map<String, CandleState> getCandleStates() {
        return candleStates;
    }

    public void update(String market, double value) {
        CandleState candleState = candleStates.get(market);

        if (candleState == null) {
            candleStates.put(market, new CandleState(value));
            return;
        }

        candleState.update(value);
    }

    public void saveAllState() {
        if (candleStates.isEmpty())
            return;
    }
}
