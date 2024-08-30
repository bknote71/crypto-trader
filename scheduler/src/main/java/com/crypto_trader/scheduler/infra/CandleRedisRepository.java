package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.domain.Candle;
import com.crypto_trader.scheduler.domain.CandleState;
import com.crypto_trader.scheduler.domain.CandleUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.crypto_trader.scheduler.global.constant.RedisConst.MINUTE_CANDLE;

@Repository
public class CandleRedisRepository {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CandleRedisRepository(ReactiveRedisTemplate<String, String> redisTemplate,
                                 ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveMinuteCandle(String market, CandleState candleState, CandleUnit unit) {
        try {
            String key = unit + MINUTE_CANDLE + market;
            String candle = objectMapper.writeValueAsString(candleState);
            redisTemplate.opsForList()
                    .rightPush(key, candle)
                    .subscribe();
        } catch (JsonProcessingException e) {
            // TODO: error handling
            System.out.println("parse error");
        }
    }

    public Mono<List<CandleState>> getOneMinuteCandle(String market) {
        String key = CandleUnit.ONEMINUTE + MINUTE_CANDLE + market;
        return redisTemplate.opsForList().range(key, 0, -1)
                .<CandleState>handle((str, sink) -> {
                    try {
                        sink.next(objectMapper.readValue(str, CandleState.class));
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                })
                .collectList();
    }
}
