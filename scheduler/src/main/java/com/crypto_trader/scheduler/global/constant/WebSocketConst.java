package com.crypto_trader.scheduler.global.constant;

import java.net.URI;
import java.util.UUID;

public class WebSocketConst {

    // static field
    public static final URI WEBSOCKET_URL = URI.create("wss://api.upbit.com/websocket/v1");
    public static final String SOCKET_ID = UUID.randomUUID().toString();
}
