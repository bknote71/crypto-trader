package com.crypto_trader.api_server.application;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Getter
public class LatestCandleDatas {
    private final Deque<byte[]> deque;
    private final Sinks.Many<byte[]> sink;

    @Setter
    private long count = 0;

    LatestCandleDatas() {
        this.deque = new ArrayDeque<>();
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    void addCandle(byte[] candle) {
        if (deque.size() >= 100) {
            deque.pollFirst();
        }
        deque.addLast(candle);
        sink.tryEmitNext(candle);
    }

    Flux<byte[]> asFlux() {
        return sink.asFlux();
    }

    public List<byte[]> datas() {
        return deque.stream().toList();
    }

}