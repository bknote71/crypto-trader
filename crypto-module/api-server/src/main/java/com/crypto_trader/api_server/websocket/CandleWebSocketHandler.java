package com.crypto_trader.api_server.websocket;

import com.crypto_trader.api_server.application.dto.CandleRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.Disposable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.lettuce.core.protocol.CommandType.RPUSH;

@Slf4j
@Component
public class CandleWebSocketHandler extends JsonWebSocketHandler<CandleRequestDto, String> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final Map<String, Disposable> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    public CandleWebSocketHandler(ReactiveRedisTemplate<String, String> redisTemplate,
                                  ObjectMapper objectMapper) {
        super(objectMapper);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doHandleMessage(CandleRequestDto instance, WebSocketSession session) {
        Disposable disposable = sessionMap.get(session.getId());
        if (disposable != null) {
            disposable.dispose();
        }

        String key = instance.makeKey();
        redisTemplate.opsForList()
                .range(key, 0, -1) // 1. 최초에는 다 가져온다.
                .doOnNext(value -> sendJsonMessage(value, session))
                .doOnComplete(() -> subscribeLast(key, session)) // 2. 이후에 실시간 데이터만 가져오도록 한다.
                .subscribe();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Disposable disposable = sessionMap.remove(session.getId());
        if(disposable != null) {
            disposable.dispose();
        }
    }

    public void subscribeLast(String key, WebSocketSession session) {
        PatternTopic topic = new PatternTopic("__keyspace@0__:" + key);
        Disposable subscribe = redisTemplate
                .listenTo(topic)
                .subscribe(value -> {
                    log.debug("Received event: {}", value.getMessage());
                    if (!value.getMessage().equals(RPUSH.name().toLowerCase()))
                        return;
                    String message = redisTemplate.opsForList()
                            .range(key, -1, -1)
                            .blockFirst();
                    sendJsonMessage(message, session);
                });
        sessionMap.put(session.getId(), subscribe);
    }
}
