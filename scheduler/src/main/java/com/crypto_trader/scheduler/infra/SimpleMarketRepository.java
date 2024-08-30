package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.domain.Market;
import com.crypto_trader.scheduler.domain.event.MarketsUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SimpleMarketRepository {
    private final Map<String, Market> markets = new HashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SimpleMarketRepository(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public synchronized void saveMarkets(List<Market> newMarketList) {
        Map<String, Market> newMarkets = newMarketList.stream()
                .collect(Collectors.toMap(Market::getMarket, market -> market));

        boolean isModified = !markets.keySet().equals(newMarkets.keySet()) || !markets.equals(newMarkets);

        markets.keySet().retainAll(newMarkets.keySet());
        markets.putAll(newMarkets);

        if (isModified) {
            eventPublisher.publishEvent(new MarketsUpdateEvent(this));
        }
    }

    public List<Market> getAllMarkets() {
        return new ArrayList<>(markets.values());
    }

    public List<String> getAllMarketCodes() {
        return new ArrayList<>(markets.keySet());
    }
}
