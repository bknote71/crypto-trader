package com.crypto_trader.api_server.dto;


import com.crypto_trader.api_server.domain.CandleUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandleRequestDto {
    private String market;
    private CandleUnit unit;

    public CandleRequestDto() {
    }

    public CandleRequestDto(String market, CandleUnit unit) {
        this.market = market;
        this.unit = unit;
    }

    public String makeKey() {
        return unit + ":minute_candle:" + market;
    }
}
