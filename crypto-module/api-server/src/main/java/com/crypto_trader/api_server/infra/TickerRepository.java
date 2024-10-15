package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.config.redis.ReactiveRedisPubSubTemplate;
import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.events.TickerChangeEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crypto_trader.api_server.global.constant.Constants.TICKER;

@Repository
public class TickerRepository {

    private final ReactiveRedisPubSubTemplate<String> pubSubTemplate;
    private final SimpleMarketRepository simpleMarketRepository;
    private final ApplicationEventPublisher publisher;

    private final Map<String, Ticker> tickers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        initTickers();
    }

    @Autowired
    public TickerRepository(ReactiveRedisPubSubTemplate<String> pubSubTemplate,
                            SimpleMarketRepository simpleMarketRepository,
                            ApplicationEventPublisher publisher) {
        this.pubSubTemplate = pubSubTemplate;
        this.simpleMarketRepository = simpleMarketRepository;
        this.publisher = publisher;
    }

    public void save(Ticker ticker) {
        tickers.put(ticker.getMarket(), ticker);
        try {
            publisher.publishEvent(new TickerChangeEvent(this, ticker));
        } catch (IllegalStateException e) { } // ignore

    }

    public Ticker findTickerByMarket(String marketCode) {
        return tickers.get(marketCode);
    }

    public List<Ticker> findAllTickers() {
        return new ArrayList<>(tickers.values());
    }

    public Flux<? extends ReactiveSubscription.Message<String, String>> getChannel() {
        return pubSubTemplate
                .select()
                .listenToChannel(TICKER);
    }

    private void initTickers() {
        simpleMarketRepository
                .marketCodesUpdates()
                .doOnNext(marketCodes -> {
                    for (String marketCode : marketCodes) {
                        Ticker ticker = new Ticker(marketCode, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                        tickers.put(marketCode, ticker);
                    }
                })
                .subscribe();
    }
}
