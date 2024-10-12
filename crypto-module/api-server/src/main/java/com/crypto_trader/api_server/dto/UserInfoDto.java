package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.entities.UserEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class UserInfoDto {
    // Account
    private AccountDto account;

    // Crypto Asset
    private List<CryptoAssetDto> assets;

    public UserInfoDto(AccountDto account, List<CryptoAssetDto> assets) {
        this.account = account;
        this.assets = assets;
    }

    public static UserInfoDto from(UserEntity user) {
        List<CryptoAssetDto> assets = user.getAssets().stream().map(a -> new CryptoAssetDto(a.getMarket(), a.getAmount(), a.getAvgPrice()))
                .toList();
        return new UserInfoDto(
                AccountDto.from(user.getAccount()),
                assets
        );
    }
}
