package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.events.TickerChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crypto_trader.api_server.global.constant.Constants.TICKER;

@Repository
public class TickerRepository {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher publisher;

    private final Map<String, Ticker> tickers = new ConcurrentHashMap<>();

    @Autowired
    public TickerRepository(ReactiveRedisTemplate<String, String> redisTemplate,
                            ApplicationEventPublisher publisher) {
        this.redisTemplate = redisTemplate;
        this.publisher = publisher;
    }

    public void save(Ticker ticker) {
        tickers.put(ticker.getCode(), ticker);
        publisher.publishEvent(new TickerChangeEvent(this, ticker));
    }

    public Ticker findTickerByMarket(String marketCode) {
        return tickers.get(marketCode);
    }

    public List<Ticker> findAllTickers() {
        return new ArrayList<>(tickers.values());
    }

    public Flux<? extends ReactiveSubscription.Message<String, String>> getChannel() {
        return redisTemplate
                .listenToChannel(TICKER);
    }
}