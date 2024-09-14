package com.crypto_trader.api_server.application.dto;

import com.crypto_trader.api_server.domain.entities.UserEntity;
import lombok.Getter;

@Getter
public class UserInfoDto {
    // Account
    private AccountDto account;

    // Crypto Asset
    private CryptoAssetsDto assets;

    public UserInfoDto(AccountDto account, CryptoAssetsDto assets) {
        this.account = account;
        this.assets = assets;
    }

    public static UserInfoDto from(UserEntity user) {
        return new UserInfoDto(
                AccountDto.from(user.getAccount()),
                CryptoAssetsDto.from(user.getAssets())
        );
    }
}
