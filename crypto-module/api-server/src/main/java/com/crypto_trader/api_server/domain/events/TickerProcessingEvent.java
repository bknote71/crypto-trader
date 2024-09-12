package com.crypto_trader.api_server.domain.events;

import com.crypto_trader.api_server.domain.Ticker;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TickerProcessingEvent extends ApplicationEvent {
    private final Ticker ticker;

    public TickerProcessingEvent(Object source, Ticker ticker) {
        super(source);
        this.ticker = ticker;
    }

}
