package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.config.redis.ReactiveRedisPubSubTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Repository;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

import static com.crypto_trader.api_server.global.constant.Constants.MARKET;

@Repository
public class SimpleMarketRepository {

    private final ReactiveRedisPubSubTemplate<String> pubSubTemplate;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private List<String> marketCodes = new ArrayList<>();

    private final Sinks.Many<List<String>> sink = Sinks.many().replay().latest(); // 최신값 받도록
    private Disposable subscription;

    @Autowired
    public SimpleMarketRepository(ReactiveRedisPubSubTemplate<String> pubSubTemplate,
                                  ReactiveRedisTemplate<String, String> redisTemplate,
                                  ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        getMarket();

        PatternTopic topic = new PatternTopic("__keyspace@0__:" + MARKET);
        pubSubTemplate
                .select()
                .listenTo(topic)
                .subscribe(value -> getMarket());
    }

    public Flux<List<String>> marketCodesUpdates() {
        return sink.asFlux();
    }

    private void getMarket() {
        if (this.subscription != null)
            this.subscription.dispose();

        this.subscription = redisTemplate
                .opsForValue()
                .get(MARKET)
                .subscribe(markets -> {
                    try {
                        this.marketCodes = objectMapper.readValue(markets, new TypeReference<List<String>>() {
                        });
                        sink.tryEmitNext(this.marketCodes);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
