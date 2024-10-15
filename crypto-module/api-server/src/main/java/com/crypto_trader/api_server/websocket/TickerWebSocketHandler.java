package com.crypto_trader.api_server.websocket;

import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.events.TickerChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TickerWebSocketHandler extends JsonWebSocketHandler<Void, Ticker> {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    public TickerWebSocketHandler(MeterRegistry meterRegistry, ObjectMapper objectMapper) {
        super(objectMapper);
        Gauge.builder("websocket.ticker.sessions.count", sessions, List::size)
                .description("Current number of active WebSocket sessions")
                .register(meterRegistry);
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

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @EventListener
    public void onTickerChange(TickerChangeEvent event) throws JsonProcessingException {
        String ticker = convertToV(event.getTicker());
        List<WebSocketSession> failedSessions = new CopyOnWriteArrayList<>();

        sessions
                .parallelStream()
                .forEach(session -> {
                    try {
                        sendJsonMessage(ticker, session);
                    } catch (IOException e) {
                        failedSessions.add(session);
                    }
                });

        sessions.removeAll(failedSessions);
    }
}
