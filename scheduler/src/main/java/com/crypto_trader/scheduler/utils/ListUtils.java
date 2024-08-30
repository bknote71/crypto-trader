package com.crypto_trader.scheduler.utils;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ListUtils {
    public static <T> Stream<List<T>> chunk(List<T> list, int chunkSize) {
        return IntStream.range(0, (list.size() + chunkSize - 1) / chunkSize)
                .mapToObj(i -> list.subList(i * chunkSize, Math.min(list.size(), (i + 1) * chunkSize)));
    }
}
