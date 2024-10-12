package com.crypto_trader.api_server.dto;

public class CryptoDto {
    private String market;
    private String nameKr;
    private String nameEn;

    public CryptoDto(String market, String nameKr, String nameEn) {
        this.market = market;
        this.nameKr = nameKr;
        this.nameEn = nameEn;
    }

    public CryptoDto(String market) {
        this.market = market;
        this.nameKr = market;
        this.nameEn = market;
    }

    public String getMarket() {
        return market;
    }

    public String getNameKr() {
        return nameKr;
    }

    public String getNameEn() {
        return nameEn;
    }
}
