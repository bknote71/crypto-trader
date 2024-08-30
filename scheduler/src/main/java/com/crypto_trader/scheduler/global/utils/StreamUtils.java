package com.crypto_trader.scheduler.global.utils;

import java.util.function.Consumer;
import java.util.function.Function;

public class StreamUtils {

    // 체크 예외를 런타임 예외로 변환하는 Function 래퍼
    public static <T, R> Function<T, R> wrapFunction(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    // 체크 예외를 런타임 예외로 변환하는 Consumer 래퍼
    public static <T> Consumer<T> wrapConsumer(ThrowingConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    // 체크 예외를 던지는 함수형 인터페이스
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    // 체크 예외를 던지는 소비자 함수형 인터페이스
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }
}
