package com.crypto_trader.api_server.config;

import com.crypto_trader.api_server.websocket.CandleWebSocketHandler;
import com.crypto_trader.api_server.websocket.TickerWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@Configuration
public class WebSocketHandler implements WebSocketConfigurer {

    private final TickerWebSocketHandler tickerWebSocketHandler;
    private final CandleWebSocketHandler candleWebSocketHandler;

    public WebSocketHandler(TickerWebSocketHandler tickerWebSocketHandler, CandleWebSocketHandler candleWebSocketHandler) {
        this.tickerWebSocketHandler = tickerWebSocketHandler;
        this.candleWebSocketHandler = candleWebSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(tickerWebSocketHandler, "/ticker")
                .setAllowedOrigins("*");

        registry
                .addHandler(candleWebSocketHandler, "/candle")
                .setAllowedOrigins("*");
    }
}
