package com.crypto_trader.api_server.application.dto;

import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.infra.TickerRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TickerRequestDto {

    @JsonProperty("code")
    private String market;
    @JsonProperty("trade_price")
    private double tradePrice;
    @JsonProperty("acc_trade_price_24h")
    private double accTradePrice24h;

    @Builder
    public TickerRequestDto(String market, double tradePrice, double accTradePrice24h) {
        this.market = market;
        this.tradePrice = tradePrice;
        this.accTradePrice24h = accTradePrice24h;
    }

    public static TickerRequestDto from(Ticker ticker) {
        return TickerRequestDto.builder()
                .market(ticker.getMarket())
                .tradePrice(ticker.getTradePrice())
                .accTradePrice24h(ticker.getAccTradePrice24h())
                .build();
    }
}
