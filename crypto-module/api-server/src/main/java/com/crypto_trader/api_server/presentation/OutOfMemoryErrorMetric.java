package com.crypto_trader.api_server.presentation;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class OutOfMemoryErrorMetric {

    @Setter
    private static volatile boolean oomOccurred = false;

    public OutOfMemoryErrorMetric(MeterRegistry meterRegistry) {
        Gauge.builder("application.oom.status", OutOfMemoryErrorMetric::isOomOccurred)
                .description("Indicates if an OutOfMemoryError has occurred (1 for true, 0 for false)")
                .register(meterRegistry);
    }

    public static int isOomOccurred() {
        return oomOccurred ? 1 : 0;
    }
}