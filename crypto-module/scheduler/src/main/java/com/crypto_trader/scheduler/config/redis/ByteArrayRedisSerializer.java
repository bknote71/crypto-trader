package com.crypto_trader.scheduler.config.redis;


import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ByteArrayRedisSerializer implements RedisSerializer<byte[]> {

    @Override
    public byte[] serialize(byte[] bytes) throws SerializationException {
        return bytes; // byte[] 그대로 리턴
    }

    @Override
    public byte[] deserialize(byte[] bytes) throws SerializationException {
        return bytes; // byte[] 그대로 리턴
    }
}