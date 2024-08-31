package com.crypto_trader.api_server.application.dto;

import com.crypto_trader.api_server.domain.entities.Order;

public class OrderResponseDto {
    public static OrderResponseDto toDto(Order order) {
        return new OrderResponseDto();
    }
}
