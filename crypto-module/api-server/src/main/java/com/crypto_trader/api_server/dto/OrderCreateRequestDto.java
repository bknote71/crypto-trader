package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.OrderSide;
import com.crypto_trader.api_server.domain.entities.Order;

public class OrderCreateRequestDto {
    private String market;
    private String side;
    private Number volume;
    private Number price;

    public String getMarket() {
        return market;
    }

    public String getSide() {
        return side;
    }

    public Number getVolume() {
        return volume;
    }

    public Number getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "OrderCreateRequestDto{" +
                "market='" + market + '\'' +
                ", side='" + side + '\'' +
                ", volume=" + volume +
                ", price=" + price +
                '}';
    }

    public Order toEntity() {
        return new Order(market, OrderSide.valueOf(side), volume, price);
    }
}
