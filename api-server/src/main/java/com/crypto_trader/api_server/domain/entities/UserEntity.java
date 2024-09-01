package com.crypto_trader.api_server.domain.entities;

import com.crypto_trader.api_server.domain.Account;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @Embedded
    private Account account;

    // 보유한 크립토
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CryptoAsset> assets = new ArrayList<>();

    public UserEntity() {}

    public UserEntity(String username) {
        this.username = username;
        this.account = new Account();
    }

    // getter

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Account getAccount() {
        return account;
    }

    public List<CryptoAsset> getAssets() {
        return assets;
    }

    public CryptoAsset findCryptoAssetByMarket(String market) {
        return assets.stream()
                .filter(c -> c.getMarket().equals(market))
                .findFirst()
                .orElse(null);
    }


}
