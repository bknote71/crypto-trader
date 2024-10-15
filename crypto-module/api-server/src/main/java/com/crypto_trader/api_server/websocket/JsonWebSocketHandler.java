package com.crypto_trader.api_server.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class JsonWebSocketHandler<T, V> extends TextWebSocketHandler {

    protected final ObjectMapper objectMapper;

    public JsonWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            T instance = objectMapper.readValue(message.getPayload(), getTypeReference());
            doHandleMessage(instance, session);
        } catch (IOException e) {
            System.out.println("e: " + e.getMessage());
        }
    }

    // abstract method
    public abstract void doHandleMessage(T instance, WebSocketSession session);

    protected void sendMessage(V message, WebSocketSession session) {
        try {
            String payload = convertToV(message);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendJsonMessage(String json, WebSocketSession session) throws IOException {
        if (!session.isOpen())
            return;

        session.sendMessage(new TextMessage(json));
    }

    protected String convertToV(V message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }

    // private
    private TypeReference<T> getTypeReference() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return new TypeReference<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return superClass.getActualTypeArguments()[0];
            }
        };
    }
}
