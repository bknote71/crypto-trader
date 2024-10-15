package com.crypto_trader.scheduler.application;

import com.crypto_trader.scheduler.config.redis.ReactiveRedisPubSubTemplate;
import com.crypto_trader.scheduler.domain.Ticker;
import com.crypto_trader.scheduler.domain.entity.Candle;
import com.crypto_trader.scheduler.infra.CandleMongoRepository;
import com.crypto_trader.scheduler.infra.SimpleCandleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.crypto_trader.scheduler.domain.CandleUnit.ONEMINUTE;
import static com.crypto_trader.scheduler.global.constant.RedisConst.*;
import static com.crypto_trader.scheduler.proto.DataModel.*;

@Slf4j
@Service
public class CandleService {

    private final MarketService marketService;
    private final TickerService tickerService;

    private final SimpleCandleRepository candleRepository;
    private final CandleMongoRepository candleMongoRepository;
    private final ReactiveRedisPubSubTemplate<String> stringPubSubTemplate;
    private final ReactiveRedisPubSubTemplate<byte[]> bytesPubsubTemplate;

    private final ObjectMapper objectMapper;

    public CandleService(MarketService marketService,
                         TickerService tickerService,
                         SimpleCandleRepository candleRepository,
                         CandleMongoRepository candleMongoRepository,
                         ReactiveRedisPubSubTemplate<String> stringPubSubTemplate,
                         ReactiveRedisPubSubTemplate<byte[]> bytesPubsubTemplate,
                         ObjectMapper objectMapper) {
        this.marketService = marketService;
        this.tickerService = tickerService;
        this.candleRepository = candleRepository;
        this.candleMongoRepository = candleMongoRepository;
        this.stringPubSubTemplate = stringPubSubTemplate;
        this.bytesPubsubTemplate = bytesPubsubTemplate;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        marketService.renewalMarkets();

        cleanRedisDB()
                .then(Mono.fromRunnable(this::initRedisCandleFromMongo))
                .subscribe(success -> log.debug("Redis initialized with Mongo data."),
                        error -> log.debug("Error initializing Redis: {}", error.getMessage()));

        // Redis Ticker 구독 설정
        stringPubSubTemplate
                .master
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

    private void initRedisCandleFromMongo() {
        log.debug("Starting initRedisCandleFromMongo...");
        System.out.println("starting initRedisCandleFromMongo...");

        List<String> markets = marketService.getAllMarketCodes();
        List<Candle> candles = new ArrayList<>();

//        markets.parallelStream().forEach(m -> {
//            log.debug("Fetching candles for market: {}", m);
//            List<Candle> candlesByMarket = candleMongoRepository.findCandlesByMarket(m).subList(0, 1000);
//            candles.addAll(candlesByMarket);
//        });

        System.out.println("all candle datas fetched");

        // 각 캔들 데이터를 Redis에 저장
        // TODO: 5분봉, 10분봉 추가
        candles.forEach(candle -> {
            String key = ONEMINUTE + ":minute_candle:" + candle.getMarket();
            try {
                PCandle pCandle = new PCandle.Builder()
                        .setOpen(candle.getOpen())
                        .setClose(candle.getClose())
                        .setHigh(candle.getHigh())
                        .setLow(candle.getLow())
                        .setVolume(candle.getVolume())
                        .setTime(candle.getTime().toString())
                        .build();

                bytesPubsubTemplate
                        .master
                        .opsForList()
                        .rightPush(key, pCandle.toByteArray())  // Redis 리스트에 저장
                        .subscribe();
            } catch (Exception e) {
                log.debug("Error serializing candle data: {}", e.getMessage());
            }
        });

        tickerService.fetchStart();
    }

    // Redis 데이터베이스에서 "market" 키를 제외한 모든 데이터를 삭제
    private Mono<Void> cleanRedisDB() {
        return stringPubSubTemplate
                .master
                .keys("*")
                .filter(key -> !key.equals(MARKET))
                .flatMap(stringPubSubTemplate.master::delete)
                .then(Mono.just("Redis cache cleared except 'market' key on startup."))
                .doOnSuccess(log::debug)
                .doOnError(Throwable::printStackTrace)
                .then();
    }
}
