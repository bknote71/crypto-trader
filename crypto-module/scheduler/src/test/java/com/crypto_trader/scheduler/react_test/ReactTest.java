package com.crypto_trader.scheduler.react_test;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static org.junit.jupiter.api.Assertions.*;

public class ReactTest {

    @Test
    void sinkUniTest() {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

        sink.asFlux().subscribe(data -> System.out.println("Subscriber 1: " + data));
        sink.tryEmitNext("Data 1");
        sink.asFlux()
                .doOnError(e -> assertEquals(IllegalStateException.class, e.getClass(), "Sink unicast invokes error when multi subscribe"))
                .subscribe(data -> System.out.println("Subscriber 2: " + data));
        sink.tryEmitNext("Data 2");
    }

    @Test
    void sinkMultiTest() {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        sink.asFlux().subscribe(data -> System.out.println("Subscriber 1: " + data));
        sink.tryEmitNext("Data 1");
        sink.asFlux().subscribe(data -> System.out.println("Subscriber 2: " + data));
        sink.tryEmitNext("Data 2");
    }

    @Test
    void sinkMultiTest2() {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        sink.tryEmitNext("Data 1");
        sink.asFlux().subscribe(data -> System.out.println("Subscriber 1: " + data));
        sink.asFlux().subscribe(data -> System.out.println("Subscriber 2: " + data));
        sink.tryEmitNext("Data 2");
    }

    @Test
    void sinkReplayTest() {
        Sinks.Many<String> sink = Sinks.many().replay().latest();

        sink.asFlux().subscribe(data -> System.out.println("Subscriber 1: " + data));
        sink.tryEmitNext("Data 1");
        sink.asFlux().subscribe(data -> System.out.println("Subscriber 2: " + data));
        sink.tryEmitNext("Data 2");
    }

    @Test
    void sinkReplayTest2() {
        Sinks.Many<String> sink = Sinks.many().replay().latest();

        sink.asFlux().subscribe(data -> System.out.println("Subscriber 1: " + data));
        sink.tryEmitNext("Data 1");
        Flux<String> stringFlux = sink.asFlux().doOnNext((data) -> {
            System.out.println("send "+data);
        });
        stringFlux.subscribe(data -> System.out.println("Subscriber 2: " + data));
        sink.tryEmitNext("Data 2");
    }

    @Test
    void thenTest() {
        Mono.just("Data saved")
                .doOnNext(value -> System.out.println(value))  // 데이터가 저장되었다는 로그 출력
                .then(Mono.just("Notification sent"))  // 그 후 알림을 보내는 새로운 Mono를 처리
                .subscribe(notification -> System.out.println(notification));
    }

    @Test
    void flatMapTest() {
        Mono.just("Data saved")
                .doOnNext(value -> System.out.println(value))  // 데이터가 저장되었다는 로그 출력
                .flatMap(value -> Mono.just("handle "+value))  // 이전 데이터 활용해서 새로운 Mono 생성
                .subscribe(notification -> System.out.println(notification));
    }
}
