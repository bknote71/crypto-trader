package com.crypto_trader.scheduler.domain.event;

import org.springframework.context.ApplicationEvent;

public class MarketsUpdateEvent extends ApplicationEvent {
    public MarketsUpdateEvent(Object source) {
        super(source);
    }
}
