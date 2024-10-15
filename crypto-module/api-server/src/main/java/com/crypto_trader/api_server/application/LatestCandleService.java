package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.config.redis.ReactiveRedisPubSubTemplate;
import com.crypto_trader.api_server.infra.SimpleMarketRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;

import static io.lettuce.core.protocol.CommandType.RPUSH;

@Service
public class LatestCandleService {

    private final ReactiveRedisPubSubTemplate<byte[]> pubSubTemplate;
    private final ReactiveRedisTemplate<String, byte[]> redisTemplate;
    private Map<String, LatestCandleDatas> latestCandles;

    public LatestCandleService(ReactiveRedisPubSubTemplate<byte[]> pubSubTemplate,
                               ReactiveRedisTemplate<String, byte[]> redisTemplate,
                               SimpleMarketRepository simpleMarketRepository) {
        this.pubSubTemplate = pubSubTemplate;
        this.redisTemplate = redisTemplate;

        simpleMarketRepository
                .marketCodesUpdates()
                .subscribe(codes -> {
                    this.latestCandles = new HashMap<>();

                    codes.forEach(code -> {
                        LatestCandleDatas latestCandleDatas = new LatestCandleDatas();
                        this.latestCandles.put(code, latestCandleDatas);
                        subscribeRealtimeCandle(code, latestCandleDatas);
                    });
                });
    }

    private void subscribeRealtimeCandle(String code, LatestCandleDatas latestCandleDatas) {
        // 현재는 1분 캔들만 (TODO: 5분, 10분, ..)
        String key = "ONEMINUTE:minute_candle:" + code;
        PatternTopic topic = new PatternTopic("__keyspace@0__:" + key);
        pubSubTemplate
                .select()
                .listenTo(topic)
                .subscribe(value -> {
                    String message = new String(value.getMessage());
                    if (!message.equals(RPUSH.name().toLowerCase()))
                        return;

                    // llen
                    redisTemplate
                            .opsForList()
                            .size(key)
                            .subscribe(latestCandleDatas::setCount);

                    // lrange
                    redisTemplate
                            .opsForList()
                            .range(key, -1, -1)
                            .subscribe(latestCandleDatas::addCandle);
                });
    }

    public LatestCandleDatas getLatestCandle(String code) {
        return latestCandles.get(code);
    }
}
