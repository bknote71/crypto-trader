package com.crypto_trader.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticker {
    private String code;

    @JsonProperty("trade_price")
    private double tradePrice;

    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h;

    public Ticker() {}

    public Ticker(String code, double tradePrice, double accTradePrice24h) {
        this.code = code;
        this.tradePrice = tradePrice;
        this.accTradePrice24h = accTradePrice24h;
    }

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
