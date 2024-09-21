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
        subscribeLast(key, session);

//        redisTemplate.opsForList()
//                .range(key, 0, -1)
//                .doOnNext(value -> sendJsonMessage(value, session)) //
//                .doOnComplete(() -> subscribeLast(key, session)) // 2. 이후에 실시간 데이터만 가져오도록 한다.
//                .subscribe();
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
                    redisTemplate.opsForList()
                            .range(key, -1, -1)
                            .subscribe(candle -> {
                                System.out.println("candle data: " + candle);
                                sendJsonMessage(candle, session);
                            });

                    // .blockFirst();
                    // - 리액티브 스트림을 동기식으로 처리: 이벤트 루프 블로킹 (이벤트 루프가 해당 작업에 묶여서 더 이상 새로운 명령어를 처리하지 못한다.)
                    // - Netty의 이벤트 루프 중 하나를 차단? 하는 듯 하다. (Netty는 여러 개의 스레드로 구성된 이벤트 루프 그룹(EventLoopGroup)을 사용하여 비동기 작업을 처리)
                    // - 이로 인해 해당 스레드가 맡고 있던 다른 작업들이 대기 상태에 놓이게 된다.
                    // - 즉 다른 Redis command가 처리되지 않거나 실행 자체가 지연되며, 결국 타임아웃이 발생하게 된다고 한다.
                });
        sessionMap.put(session.getId(), subscribe);
    }
}
