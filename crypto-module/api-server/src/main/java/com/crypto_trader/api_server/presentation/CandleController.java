package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.dto.CandleRequestDto;
import com.crypto_trader.api_server.application.dto.CandleResponseDto;
import com.crypto_trader.api_server.domain.CandleUnit;
import com.crypto_trader.api_server.domain.entities.Candle;
import com.crypto_trader.api_server.infra.CandleMongoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CandleController {

    private final ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate;

    // TEMP
    private List<byte[]> tempList;

    @Autowired
    public CandleController(ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate) {
        this.byteArrayRedisTemplate = byteArrayRedisTemplate;

        String key =  "ONEMINUTE:minute_candle:KRW-BTC";
        this.tempList = byteArrayRedisTemplate.opsForList()
                .range(key, 0, -1)
                .collectList()
                .block();

        int targetSize = 1000;
        while (tempList.size() < targetSize) {
            tempList.addAll(tempList);
        }
        tempList = tempList.subList(0, targetSize);
    }

    // using protobuf
    @GetMapping("/api/all-candles")
    public Mono<List<byte[]>> getAllCandlesP(@ModelAttribute CandleRequestDto dto) {
        String key = dto.makeKey();
        return byteArrayRedisTemplate.opsForList()
                .range(key, 0, -1)
                .collectList();
    }

    // TEMP
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class CandlesInfo {
        double startDate;
        double endDate;
        int count;
        List<byte[]> candles;
    }

    @GetMapping("/api/candles-info")
    public CandlesInfo getCandlesInfo(@ModelAttribute CandleRequestDto dto) {
        // 100개 정도 임의 데이터 만들기
        List<byte[]> bytes = tempList.subList(0, 100);
        double startDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / 60.0;
        double endDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / 60.0; // 어차피 필요 없음
        int count = 1000;

        CandlesInfo info = new CandlesInfo(
                startDate,
                endDate,
                count,
                bytes
        );

        return info;
    }

    @GetMapping("/api/candles")
    public List<byte[]> getCandles(@RequestParam("market") String market,
                                   @RequestParam("unit")  CandleUnit unit,
                                   @RequestParam("start") double startDate,
                                   @RequestParam("end") double endDate) {
        return tempList.subList(100, 200);
    }
}
