package com.crypto_trader.scheduler.application;

import com.crypto_trader.scheduler.infra.TickerWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;

import static com.crypto_trader.scheduler.global.constant.WebSocketConst.WEBSOCKET_URL;

@Service
public class TickerService {

    private final TickerWebSocketHandler tickerWebSocketHandler;

    @Autowired
    public TickerService(TickerWebSocketHandler tickerWebSocketHandler) {
        this.tickerWebSocketHandler = tickerWebSocketHandler;
    }

    public void fetchAllTickers(List<String> marketCodes) {
        tickerWebSocketHandler.fetchAllTicker(marketCodes);
    }

    public void fetchStart() {
        WebSocketConnectionManager webSocketConnectionManager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                tickerWebSocketHandler,
                WEBSOCKET_URL
        );
        webSocketConnectionManager.start();
    }
}
