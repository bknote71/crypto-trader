package com.crypto_trader.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Ticker {
    @JsonProperty("code")
    private String market; // 마켓 코드
    @JsonProperty("trade_price")
    private double tradePrice; // 현재가
    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h; // 24시간 누적 거래대금
    @JsonProperty("signed_change_price")
    private double signedChangePrice; // 전일 대비 값
    @JsonProperty("signed_change_rate")
    private double signedChangeRate; // 전일 대비 등락율
    @JsonProperty("high_price")
    private double highPrice; // 고가
    @JsonProperty("low_price")
    private double lowPrice; // 저가
    @JsonProperty("acc_trade_volume_24h")
    private double accTradeVolume24h; // 24시간 누적 거래량
    @JsonProperty("acc_trade_volume")
    private double accTradeVolume;

    public Ticker() {
    }

    public Ticker(final String market,
                  final double tradePrice,
                  final double accTradePrice24h,
                  final double signedChangePrice,
                  final double signedChangeRate,
                  final double highPrice,
                  final double lowPrice,
                  final double accTradeVolume24h,
                  final double accTradeVolume) {
        this.market = market;
        this.tradePrice = tradePrice;
        this.accTradePrice24h = accTradePrice24h;
        this.signedChangePrice = signedChangePrice;
        this.signedChangeRate = signedChangeRate;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.accTradeVolume24h = accTradeVolume24h;
        this.accTradeVolume = accTradeVolume;
    }
}