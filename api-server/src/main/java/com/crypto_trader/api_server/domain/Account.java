package com.crypto_trader.api_server.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Account {
    private String number; // 계좌번호
    private String currency; // 화폐 단위
    private Number balance; // 주문가능 금액
    private Number locked; // 주문 중 묶여있는 금액
    private Number avgBuyPrice; // 매수 평균가
    private String unitCurrency;

    public String getCurrency() {
        return currency;
    }

    public Number getBalance() {
        return balance;
    }

    public Number getLocked() {
        return locked;
    }

    public Number getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public String getUnitCurrency() {
        return unitCurrency;
    }

    public void unlock(Number number) {
        double lockedValue = locked.doubleValue() - number.doubleValue();
        double balanceValue = balance.doubleValue() + number.doubleValue();
        locked = lockedValue;
        balance = balanceValue;
    }

    public void lock(Number number) {
        double lockedValue = locked.doubleValue() + number.doubleValue();
        double balanceValue = balance.doubleValue() - number.doubleValue();
        locked = lockedValue;
        balance = balanceValue;
    }
}
