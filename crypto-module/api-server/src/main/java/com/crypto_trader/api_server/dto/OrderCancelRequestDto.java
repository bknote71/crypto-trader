package com.crypto_trader.api_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCancelRequestDto {
    private String market;
    private Long orderId;

    public OrderCancelRequestDto() {}

    public OrderCancelRequestDto(String market, Long orderId) {
        this.market = market;
        this.orderId = orderId;
    }
}
