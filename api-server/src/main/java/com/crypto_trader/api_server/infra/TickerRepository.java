package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.events.TickerChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crypto_trader.api_server.global.constant.Constants.TICKER;

@Repository
public class TickerRepository {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    private final Map<String, Ticker> tickers = new ConcurrentHashMap<>();

    @Autowired
    public TickerRepository(ReactiveRedisTemplate<String, String> redisTemplate,
                            ApplicationEventPublisher publisher,
                            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        redisTemplate
                .listenToChannel(TICKER)
                .subscribe(value -> {
                    try {
                        Ticker ticker = objectMapper.readValue(value.getMessage(), Ticker.class);
                        tickers.put(ticker.getCode(), ticker);
                        publisher.publishEvent(new TickerChangeEvent(this, ticker));
                    } catch (JsonProcessingException e) {
                        // TODO: error handling
                    }
                });
    }

    public Ticker findTickerByMarket(String marketCode) {
        return tickers.get(marketCode);
    }

    public List<Ticker> findAllTickers() {
        return new ArrayList<>(tickers.values());
    }
}
