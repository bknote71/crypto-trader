package com.crypto_trader.scheduler.scheduler;

import com.crypto_trader.scheduler.application.TickerService;
import com.crypto_trader.scheduler.domain.event.MarketsUpdateEvent;
import com.crypto_trader.scheduler.application.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TickerScheduler {
    private final MarketService marketService;
    private final TickerService tickerService;

    @Autowired
    public TickerScheduler(MarketService marketService, TickerService tickerService) {
        this.marketService = marketService;
        this.tickerService = tickerService;
    }

    @EventListener(MarketsUpdateEvent.class)
    public void handleMarketsUpdatedEvent(MarketsUpdateEvent event) {
        List<String> allMarketCodes = marketService.getAllMarketCodes();
        tickerService.fetchAllTickers(allMarketCodes);
    }
}
