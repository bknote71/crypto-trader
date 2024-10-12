package com.crypto_trader.api_server.dto;

import com.crypto_trader.api_server.domain.entities.Account;
import lombok.Getter;

@Getter
public class AccountDto {
    private String number; // 계좌번호
    private String currency; // 화폐 단위
    private Number balance; // 주문가능 금액
    private Number locked; // 주문 중 묶여있는 금액
    private Number avgBidPrice; // 매수 평균가

    public AccountDto(String number, String currency, Number balance, Number locked, Number avgBidPrice) {
        this.number = number;
        this.currency = currency;
        this.balance = balance;
        this.locked = locked;
        this.avgBidPrice = avgBidPrice;
    }

    public static AccountDto from(Account account) {
        return new AccountDto(
                account.getNumber(),
                account.getCurrency(),
                account.getBalance(),
                account.getLocked(),
                account.getAvgBuyPrice()
        );
    }
}
