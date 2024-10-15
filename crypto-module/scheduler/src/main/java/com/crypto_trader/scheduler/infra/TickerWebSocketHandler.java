package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.config.redis.ReactiveRedisPubSubTemplate;
import com.crypto_trader.scheduler.domain.event.FetchTickerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    private final ReactiveRedisPubSubTemplate<String> pubSubTemplate;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    // private state
    private WebSocketSession session;

    @Autowired
    public TickerWebSocketHandler(ReactiveRedisPubSubTemplate<String> pubSubTemplate,
                                  ApplicationEventPublisher publisher,
                                  ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        publisher.publishEvent(new FetchTickerEvent(this));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
        String tickerJson = decodeToString(message.getPayload());
        pubSubTemplate.publish(REDIS_TICKER, tickerJson);
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
