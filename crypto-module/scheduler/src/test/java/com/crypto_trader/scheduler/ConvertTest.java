package com.crypto_trader.scheduler;

import com.crypto_trader.scheduler.domain.entity.Candle;
import com.crypto_trader.scheduler.proto.DataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// json vs protobuf
public class ConvertTest {
    ObjectMapper objectMapper = objectMapper();

    @Test
    void jsonConvertTest() throws JsonProcessingException {
        String candle = "{\"market\":\"KRW-BTC\",\"open\":7100000.0,\"close\":7100000.0,\"high\":7100000.0,\"low\":7100000.0,\"time\":\"2024-09-22T16:10:00\",\"volume\":100.0}";

        List<String> arr = new ArrayList<>();
        for (int i = 0; i < 36000; ++i) {
            arr.add(candle);
        }

        var startTime = System.currentTimeMillis();

        // 1. 조회한 캔들을 objectMapper를 사용하여 캔들 객체로 변환
        List<CandleResponseDto> response = arr.stream().map(s -> {
                    try {
                        return objectMapper.readValue(s, CandleResponseDto.class);
                    } catch (JsonProcessingException e) {
                        System.out.println("err" + e.getMessage());
                    }
                    return null;
                })
                .toList();

        // 2. 스프링 컨트롤러에서 반환 시 자동으로 objectMapper 적용하여 json 변환
        String responseJson = objectMapper.writeValueAsString(response);

        // 3. 클라이언트에서 json decoder를 사용하여 최종 변환 (objectmapper로 대체)
        List<CandleResponseDto> results = objectMapper.readValue(responseJson, new TypeReference<List<CandleResponseDto>>() {
        });

        System.out.println("convert time: " + (System.currentTimeMillis() - startTime));
    }

    @Test
    void protobufConvertTest() throws JsonProcessingException {
        DataModel.PCandle candle = new DataModel.PCandle.Builder()
                .setOpen(71000000)
                .setClose(71000000)
                .setHigh(71000000)
                .setLow(71000000)
                .setVolume(100)
                .setTime(LocalDateTime.now().toString())
                .build();

        List<byte[]> arr = new ArrayList<>();

        for (int i = 0; i < 36000; ++i) {
            arr.add(candle.toByteArray());
        }

        var startTime = System.currentTimeMillis();

        // 1. List<byte[]> to json
        String responseJson = objectMapper.writeValueAsString(arr);

        // 클라이언트에서 [Data]로 변환 후 객체 역직렬화
        List<byte[]> results = objectMapper.readValue(responseJson, new TypeReference<List<byte[]>>() {
        });

        List<DataModel.PCandle> finalResults = results.stream().map(bytes -> {
                    try {
                        return DataModel.PCandle.parseFrom(bytes);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();


        System.out.println("convert time: " + (System.currentTimeMillis() - startTime));
    }

    public static class CandleResponseDto {
        private String market;
        private double open;
        private double close;
        private double high;
        private double low;
        private LocalDateTime time;
        private double volume;

        public CandleResponseDto() {}

        public CandleResponseDto(Candle candle) {
            this.market = candle.getMarket();
            this.open = candle.getOpen();
            this.close = candle.getClose();
            this.high = candle.getHigh();
            this.low = candle.getLow();
            this.time = candle.getTime();
            this.volume = candle.getVolume();
        }

        public String getMarket() {
            return market;
        }

        public double getOpen() {
            return open;
        }

        public double getClose() {
            return close;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public double getVolume() {
            return volume;
        }
    }

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }
}

