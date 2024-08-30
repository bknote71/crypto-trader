package com.crypto_trader.scheduler.config;

import java.net.URI;
import java.util.UUID;

public class WebSocketConst {

    // static field
    public static final URI WEBSOCKET_URL = URI.create("wss://api.upbit.com/websocket/v1");
    public static final String SOCKET_ID = UUID.randomUUID().toString();
    public static final String REDIS_TICKER_PUBLISH = "ticker";
}
