package com.crypto_trader.scheduler.application;

import com.crypto_trader.scheduler.domain.CandleState;
import com.crypto_trader.scheduler.domain.Ticker;
import com.crypto_trader.scheduler.domain.entity.Candle;
import com.crypto_trader.scheduler.infra.CandleMongoRepository;
import com.crypto_trader.scheduler.infra.SimpleCandleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.crypto_trader.scheduler.domain.CandleUnit.ONEMINUTE;
import static com.crypto_trader.scheduler.global.constant.RedisConst.*;

@Slf4j
@Service
public class CandleService {

    private final SimpleCandleRepository candleRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CandleMongoRepository candleMongoRepository;
    private final MarketService marketService;

    @Autowired
    public CandleService(SimpleCandleRepository candleRepository,
                         ReactiveRedisTemplate<String, String> redisTemplate,
                         ObjectMapper objectMapper,
                         CandleMongoRepository candleMongoRepository,
                         MarketService marketService) {
        this.candleRepository = candleRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.candleMongoRepository = candleMongoRepository;
        this.marketService = marketService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // cleanRedisDB가 완료된 후에만 initRedisCandleFromMongo 실행
        cleanRedisDB()
                .then(Mono.fromRunnable(this::initRedisCandleFromMongo))  // DB 클리어 후에 Mongo 데이터를 Redis에 초기화
                .subscribe(success -> log.debug("Redis initialized with Mongo data."),
                        error -> log.debug("Error initializing Redis: {}", error.getMessage()));

        marketService.renewalMarkets();

        // Redis Ticker 구독 설정
        redisTemplate
                .listenToChannel(REDIS_TICKER)
                .subscribe((value) -> {
                    String message = value.getMessage();
                    try {
                        Ticker ticker = objectMapper.readValue(message, Ticker.class);
                        candleRepository.update(ticker.getMarket(), ticker.getTradePrice(), ticker.getAccTradeVolume());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // MongoDB에서 모든 데이터 가져오기
    private void initRedisCandleFromMongo() {
        log.debug("Starting initRedisCandleFromMongo...");

        List<String> markets = marketService.getAllMarketCodes();
        List<Candle> candles = new ArrayList<>();

        for (String market : markets) {
            log.debug("Fetching candles for market: {}", market);
            candles.addAll(candleMongoRepository.findCandlesByMarket(market));
        }

        // 각 캔들 데이터를 Redis에 저장
        // TODO: 5분봉, 10분봉 추가
        candles.forEach(candle -> {
            String key = ONEMINUTE + ":minute_candle:" + candle.getMarket();
            try {
                CandleState candleState = new CandleState(
                        candle.getOpen(),
                        candle.getClose(),
                        candle.getHigh(),
                        candle.getLow(),
                        candle.getVolume()
                );

                String candleData = objectMapper.writeValueAsString(candleState); // Candle 객체를 JSON으로 변환
                redisTemplate.opsForList()
                        .rightPush(key, candleData)  // Redis 리스트에 저장
                        .doOnSuccess(result -> log.debug("Successfully pushed to Redis: {}", key))
                        .subscribe();
            } catch (JsonProcessingException e) {
                log.debug("Error serializing candle data: {}", e.getMessage());
            }
        });
    }

    // Redis 데이터베이스를 삭제하는 메서드
    private Mono<Void> cleanRedisDB() {
        return redisTemplate.execute(connection -> connection.serverCommands().flushAll())  // Redis에서 모든 데이터 삭제
                .then(Mono.just("Redis cache cleared on startup."))
                .doOnSuccess(log::debug)  // 성공 시 메시지 출력
                .then();  // Void 반환
    }
}
