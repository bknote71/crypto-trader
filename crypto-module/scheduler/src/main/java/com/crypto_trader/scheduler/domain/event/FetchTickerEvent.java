package com.crypto_trader.scheduler.domain.event;

import org.springframework.context.ApplicationEvent;

public class FetchTickerEvent extends ApplicationEvent {
    public FetchTickerEvent(Object source) {
        super(source);
    }
}
