package com.crypto_trader.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class CandleState {
    private double open;
    @JsonProperty("close")
    private double last;
    private double high;
    private double low;

    private LocalDateTime time;

    protected CandleState() {}

    public CandleState(double value) {
        this.open = value;
        this.last = value;
        this.high = value;
        this.low = value;
        this.time = LocalDateTime.now().withSecond(0).withNano(0);
    }

    public CandleState(double open, double last, double high, double low) {
        this.open = open;
        this.last = last;
        this.high = high;
        this.low = low;
        this.time = LocalDateTime.now().withSecond(0).withNano(0);
    }


    public static CandleState aggregate(List<CandleState> candleStates) {
        assert candleStates != null;

        double open = candleStates.get(0).getOpen();
        double last = candleStates.get(candleStates.size() - 1).getLast();
        double high = Math.max(open, last);
        double low = Math.min(open, last);

        for (CandleState candle : candleStates) {
            high = Math.max(high, candle.getHigh());
            low = Math.min(low, candle.getLow());
        }

        return new CandleState(open, last, high, low);
    }

    // getter
    public double getOpen() {
        return open;
    }

    public double getLast() {
        return last;
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

    // public method
    public void update(double value) {
        last = value;
        high = Math.max(high, value);
        low = Math.min(low, value);
    }

    public void reset() {
        open = last;
        high = last;
        low = last;
        time = LocalDateTime.now();
    }
}
