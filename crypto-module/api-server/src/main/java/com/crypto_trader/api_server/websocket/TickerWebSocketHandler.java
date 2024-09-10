package com.crypto_trader.api_server.websocket;

import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.events.TickerChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TickerWebSocketHandler extends JsonWebSocketHandler<Void, Ticker> {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    public TickerWebSocketHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void doHandleMessage(Void instance, WebSocketSession session) {
        throw new RuntimeException();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
    }

    @EventListener
    public void onTickerChange(TickerChangeEvent event) throws JsonProcessingException {
        String ticker = convertToV(event.getTicker());
        sessions
                .parallelStream()
                .forEach(session -> sendJsonMessage(ticker, session));
    }
}
