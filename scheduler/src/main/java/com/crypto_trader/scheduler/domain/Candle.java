package com.crypto_trader.scheduler.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collation = "candles")
public class Candle {

    @Id
    private String id;
    private String market;
    private double open;
    private double close;
    private double high;
    private double low;

    private LocalDateTime time;
    private double volume;

    public Candle() {}

    public Candle(String market, double open, double close, double high, double low) {
        this.market = market;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.time = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getMarket() {
        return market;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getVolume() {
        return volume;
    }
}
