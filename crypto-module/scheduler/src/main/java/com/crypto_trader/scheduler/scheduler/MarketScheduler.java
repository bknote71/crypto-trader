package com.crypto_trader.scheduler.scheduler;

import com.crypto_trader.scheduler.application.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MarketScheduler {

    private final MarketService marketService;

    @Autowired
    public MarketScheduler(MarketService marketService) {
        this.marketService = marketService;
    }

    // 초 분 시 일 월 (0 0 9 * * ?)
    @Scheduled(cron = "0 0 9 * * ?")
    public void renewalMarkets() {
        marketService.renewalMarkets();
    }
}
