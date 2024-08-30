package com.crypto_trader.scheduler.domain;

public enum CandleUnit {
    ONEMINUTE(1),
    FIVEMINUTE(5),
    TENMINUTE(10),
    ONEHOUR(60),
    ;

    final public int num;

    CandleUnit(int num) {
        this.num = num;
    }
}
