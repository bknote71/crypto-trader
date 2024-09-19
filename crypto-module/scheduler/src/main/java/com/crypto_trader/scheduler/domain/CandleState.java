package com.crypto_trader.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CandleState {
    private double open;
    @JsonProperty("close")
    private double last;
    private double high;
    private double low;

    private double prevVolume;
    private double volume; // 분당 거래량

    private LocalDateTime time;

    protected CandleState() {}

    public CandleState(double value) {
        this.open = value;
        this.last = value;
        this.high = value;
        this.low = value;
        this.time = LocalDateTime.now().withSecond(0).withNano(0);
    }

    public CandleState(double open, double last, double high, double low, double volume) {
        this.open = open;
        this.last = last;
        this.high = high;
        this.low = low;
        this.time = LocalDateTime.now().withSecond(0).withNano(0);
        this.volume = volume;
    }


    public static CandleState aggregate(List<CandleState> candleStates) {
        assert candleStates != null;

        double open = candleStates.get(0).getOpen();
        double last = candleStates.get(candleStates.size() - 1).getLast();
        double high = Math.max(open, last);
        double low = Math.min(open, last);
        double volume = candleStates.stream()
                .map(candle -> candle.volume)
                .reduce(0.0, Double::sum);

        for (CandleState candle : candleStates) {
            high = Math.max(high, candle.getHigh());
            low = Math.min(low, candle.getLow());
        }

        return new CandleState(open, last, high, low, volume);
    }

    // public method
    public void update(double value, double currentVolume) {
        last = value;
        high = Math.max(high, value);
        low = Math.min(low, value);
        volume += Math.max(currentVolume - prevVolume, 0);
        prevVolume = currentVolume;
    }

    public void reset() {
        open = last;
        high = last;
        low = last;
        time = LocalDateTime.now();
        volume = 0;
    }
}
