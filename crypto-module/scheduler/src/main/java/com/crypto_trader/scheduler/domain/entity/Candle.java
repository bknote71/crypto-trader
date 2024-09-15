package com.crypto_trader.scheduler.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "candle")
public class Candle {

    @Id
    private String id;
    private String market;
    private double open;
    private double close;
    private double high;
    private double low;
    private LocalDateTime time;
    private double volume;

    public Candle() {}

    public Candle(String market, double open, double close, double high, double low) {
        this.market = market;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.time = LocalDateTime.now().withSecond(0).withNano(0);
    }

}
