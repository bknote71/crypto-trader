package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.entities.Order;

public class OrderResponseDto {
    String market;

    public OrderResponseDto() {
    }

    public OrderResponseDto(String market) {
        this.market = market;
    }

    public String getMarket() {
        return market;
    }

    public static OrderResponseDto toDto(Order order) {
        return new OrderResponseDto(order.getMarket());
    }
}
