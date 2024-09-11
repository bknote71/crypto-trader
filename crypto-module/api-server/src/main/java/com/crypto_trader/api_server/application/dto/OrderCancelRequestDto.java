package com.crypto_trader.api_server.application.dto;

public class OrderCancelRequestDto {
    private String market;
    private Long orderId;

    public String getMarket() {
        return market;
    }

    public Long getOrderId() {
        return orderId;
    }
}
