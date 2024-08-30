package com.crypto_trader.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Market {
    private final String market;

    @JsonProperty("korean_name")
    private final String krName;

    @JsonProperty("english_name")
    private final String enName;

    public Market(String market, String krName, String enName) {
        this.market = market;
        this.krName = krName;
        this.enName = enName;
    }

    // getter
    public String getMarket() {
        return market;
    }

    public String getKrName() {
        return krName;
    }

    public String getEnName() {
        return enName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Market market1 = (Market) o;
        return Objects.equals(market, market1.market) && Objects.equals(krName, market1.krName) && Objects.equals(enName, market1.enName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, krName, enName);
    }
}
