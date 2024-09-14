package com.crypto_trader.api_server.application.dto;

import com.crypto_trader.api_server.domain.entities.CryptoAsset;
import lombok.Getter;

import java.util.List;

@Getter
public class CryptoAssetsDto {

    static class CryptoAssetDto {
        private String market;
        private Number amount; // 보유량
        private Number avgPrice;
    }

    List<CryptoAssetDto> cryptoAssets;

    public CryptoAssetsDto(List<CryptoAssetDto> cryptoAssets) {
        this.cryptoAssets = cryptoAssets;
    }

    public static CryptoAssetsDto from(List<CryptoAsset> assets) {
        List<CryptoAssetDto> assetsDto = assets.stream().map(a -> {
                    CryptoAssetDto dto = new CryptoAssetDto();
                    return dto;
                })
                .toList();
        return new CryptoAssetsDto(assetsDto);
    }
}
