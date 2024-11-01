package com.crypto_trader.api_server.presentation;

import lombok.Getter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OutOfMemoryErrorHandler {

    @Getter
    private static volatile boolean oomOccurred = false;

    @ExceptionHandler(OutOfMemoryError.class)
    public void handleOutOfMemoryError(OutOfMemoryError ex) {
        OutOfMemoryErrorMetric.setOomOccurred(true);
    }
}
