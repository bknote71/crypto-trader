package com.crypto_trader.scheduler.global.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringUtils {
    public static String decodeToString(ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
