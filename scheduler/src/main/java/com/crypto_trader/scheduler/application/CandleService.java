package com.crypto_trader.scheduler.application;

import com.crypto_trader.scheduler.domain.Ticker;
import com.crypto_trader.scheduler.infra.SimpleCandleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.crypto_trader.scheduler.global.constant.RedisConst.*;

@Service
public class CandleService {

    private final SimpleCandleRepository candleRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CandleService(SimpleCandleRepository candleRepository,
                         ReactiveRedisTemplate<String, String> redisTemplate,
                         ObjectMapper objectMapper) {
        this.candleRepository = candleRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        redisTemplate
                .listenToChannel(REDIS_TICKER)
                .subscribe((value) -> {
                    String message = value.getMessage();
                    try {
                        Ticker ticker = objectMapper.readValue(message, Ticker.class);
                        candleRepository.update(ticker.getCode(), ticker.getTradePrice());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
