package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.CandleUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandleRangeRequestDto {

    private String market;
    private CandleUnit unit;
    private int start;
    private int end;

    public String makeKey() {
        return unit + ":minute_candle:" + market;
    }
}
