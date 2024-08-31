package com.crypto_trader.api_server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.Disposable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TickerWebSocketHandler extends JsonWebSocketHandler<Void, String> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final Map<String, Disposable> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    public TickerWebSocketHandler(ReactiveRedisTemplate<String, String> redisTemplate,
                                  ObjectMapper objectMapper) {
        super(objectMapper);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doHandleMessage(Void instance, WebSocketSession session) {
        throw new RuntimeException();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        Disposable subscribe = redisTemplate
                .listenToChannel("ticker")
                .subscribe(value -> {
                    String message = value.getMessage();
                    sendJsonMessage(message, session);
                });

        sessionMap.put(session.getId(), subscribe);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionMap.remove(session.getId());
    }
}
