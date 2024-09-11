package com.crypto_trader.api_server.domain.events;

import com.crypto_trader.api_server.domain.Ticker;
import org.springframework.context.ApplicationEvent;

public class TickerChangeEvent extends ApplicationEvent {
    private final Ticker ticker;

    public TickerChangeEvent(Object source, Ticker ticker) {
        super(source);
        this.ticker = ticker;
    }

    public Ticker getTicker() {
        return ticker;
    }
}
