package com.crypto_trader.api_server.domain.entities;

import jakarta.persistence.*;

@Entity
public class CryptoAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String market;
    private Number amount; // 보유량
    private Number avgPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public String getMarket() {
        return market;
    }

    public Number getAmount() {
        return amount;
    }

    public void unlock(Number volume) {
        amount = volume.doubleValue() + amount.doubleValue();
    }
}
