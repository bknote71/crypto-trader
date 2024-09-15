package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.domain.CandleState;
import com.crypto_trader.scheduler.domain.CandleUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static com.crypto_trader.scheduler.global.constant.RedisConst.MINUTE_CANDLE;

@Slf4j
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

            // 1. Redis에 데이터를 저장
            redisTemplate.opsForList()
                    .rightPush(key, candle)  // 데이터를 리스트에 추가
                    .flatMap(result -> {
                        // 2. TTL 설정 (48시간)
                        return redisTemplate.expire(key, Duration.ofHours(48));  // 48시간 TTL 설정
                    })
                    .doOnSuccess(success -> {
                    })
                    .doOnError(error -> {log.debug(error.getMessage());})
                    .subscribe();
        } catch (JsonProcessingException e) {
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
