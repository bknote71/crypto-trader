package com.crypto_trader.api_server.domain;


public class CandleRequest {
    private String market;
    private CandleUnit unit;

    public String makeKey() {
        return unit + ":minute_candle:" + market;
    }

    public String getMarket() {
        return market;
    }

    public CandleUnit getUnit() {
        return unit;
    }
}
