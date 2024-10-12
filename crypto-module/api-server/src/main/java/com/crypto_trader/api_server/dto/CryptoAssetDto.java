package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.entities.CryptoAsset;
import lombok.Getter;

import java.util.List;

@Getter
public class CryptoAssetDto {

    private String market;
    private Number amount; // 보유량
    private Number avgPrice;

    public CryptoAssetDto() {}

    public CryptoAssetDto(String market, Number amount, Number avgPrice) {
        this.market = market;
        this.amount = amount;
        this.avgPrice = avgPrice;
    }
}
