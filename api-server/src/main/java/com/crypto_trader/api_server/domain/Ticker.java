package com.crypto_trader.api_server.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticker {
    @JsonProperty("code")
    private String market;
    @JsonProperty("trade_price")
    private double tradePrice;
    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h;

    public Ticker(String market, double tradePrice, double accTradePrice24h) {
        this.market = market;
        this.tradePrice = tradePrice;
        this.accTradePrice24h = accTradePrice24h;
    }

    public String getMarket() {
        return market;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public double getAccTradePrice24h() {
        return accTradePrice24h;
    }

}
