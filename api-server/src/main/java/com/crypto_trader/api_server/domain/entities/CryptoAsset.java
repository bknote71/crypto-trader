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

    public CryptoAsset() {}

    public CryptoAsset(String market, Number amount, Number avgPrice) {
        this.market = market;
        this.amount = amount;
        this.avgPrice = avgPrice;
    }

    public String getMarket() {
        return market;
    }

    public Number getAmount() {
        return amount;
    }

    public void setUser(UserEntity user) {
        this.user = user;
        user.getAssets().add(this);
    }

    public void unlock(Number volume) {
        amount = volume.doubleValue() + amount.doubleValue();
    }

    public void bid(Number volume, Number price) {
        double avgPriceValue = avgPrice.doubleValue();
        double amountValue = amount.doubleValue();
        double volumeValue = volume.doubleValue();
        double priceValue = price.doubleValue();

        this.avgPrice = ((avgPriceValue * amountValue) + (volumeValue * priceValue)) / (amountValue + volumeValue);
        this.amount = amountValue + volumeValue;
    }

    public void ask(Number volume) {
        double avgPriceValue = avgPrice.doubleValue();
        double amountValue = amount.doubleValue();
        double volumeValue = volume.doubleValue();

        assert amountValue >= volumeValue;

        double currentTotalCost = avgPriceValue * amountValue;
        double newAmount = amountValue - volumeValue;
        double newAvgPrice = newAmount > 0 ? (currentTotalCost - (volumeValue * avgPriceValue)) / newAmount : 0;

        this.avgPrice = newAvgPrice;
        this.amount = amountValue - volumeValue;
    }

    public boolean isEmpty() {
        return amount.doubleValue() == 0;
    }
}
