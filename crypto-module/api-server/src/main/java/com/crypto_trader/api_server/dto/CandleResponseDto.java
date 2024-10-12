package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.entities.Candle;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CandleResponseDto {
    private String market;
    private double open;
    private double close;
    private double high;
    private double low;
    private LocalDateTime time;
    private double volume;

    protected CandleResponseDto() {}

    public CandleResponseDto(Candle candle) {
        this.market = candle.getMarket();
        this.open = candle.getOpen();
        this.close = candle.getClose();
        this.high = candle.getHigh();
        this.low = candle.getLow();
        this.time = candle.getTime();
        this.volume = candle.getVolume();
    }
}
