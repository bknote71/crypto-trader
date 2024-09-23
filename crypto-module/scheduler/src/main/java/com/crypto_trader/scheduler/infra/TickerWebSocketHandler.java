package com.crypto_trader.scheduler.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.crypto_trader.scheduler.global.constant.RedisConst.REDIS_TICKER;
import static com.crypto_trader.scheduler.global.constant.WebSocketConst.SOCKET_ID;
import static com.crypto_trader.scheduler.global.utils.StringUtils.*;

@Slf4j
@Component
public class TickerWebSocketHandler extends BinaryWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    // private state
    private WebSocketSession session;

    @Autowired
    public TickerWebSocketHandler(ObjectMapper objectMapper, ReactiveRedisTemplate<String, String> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
        String tickerJson = decodeToString(message.getPayload());
        redisTemplate.convertAndSend(REDIS_TICKER, tickerJson).subscribe(); // publish
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    public void fetchAllTicker(List<String> marketCodes) {
        if (session == null || !session.isOpen())
            return;


        try {
            String payload = createPayload(marketCodes);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createPayload(List<String> marketCodes) throws JsonProcessingException {
        List<Map<String, Object>> tickerPayloads = List.of(
                Map.of("ticket", SOCKET_ID),
                Map.of("type", "ticker", "codes", marketCodes)
        );

        return objectMapper.writeValueAsString(tickerPayloads);
    }
}
