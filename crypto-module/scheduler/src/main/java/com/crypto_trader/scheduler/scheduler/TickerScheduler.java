package com.crypto_trader.scheduler.scheduler;

import com.crypto_trader.scheduler.application.TickerService;
import com.crypto_trader.scheduler.domain.event.FetchTickerEvent;
import com.crypto_trader.scheduler.application.MarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TickerScheduler {
    private final MarketService marketService;
    private final TickerService tickerService;

    public TickerScheduler(MarketService marketService, TickerService tickerService) {
        this.marketService = marketService;
        this.tickerService = tickerService;
    }

    @EventListener(FetchTickerEvent.class)
    public void handleFetchTickerEvent(FetchTickerEvent event) {
        log.debug("handle fetch ticker event");
        try{
            List<String> allMarketCodes = marketService.getAllMarketCodes();
            tickerService.fetchAllTickers(allMarketCodes);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
