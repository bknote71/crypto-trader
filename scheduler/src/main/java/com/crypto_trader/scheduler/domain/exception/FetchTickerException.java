package com.crypto_trader.scheduler.domain.exception;

import java.io.IOException;

public class FetchTickerException extends RuntimeException {

    public FetchTickerException(IOException e) {
        super(e);
    }
}
