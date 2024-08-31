package com.crypto_trader.api_server.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticker {
    private String code;
    @JsonProperty("trade_price")
    private double tradePrice;
    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h;

    public String getCode() {
        return code;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public double getAccTradePrice24h() {
        return accTradePrice24h;
    }
}
