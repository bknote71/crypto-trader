package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.dto.CandleRequestDto;
import com.crypto_trader.api_server.application.dto.CandleResponseDto;
import com.crypto_trader.api_server.domain.CandleUnit;
import com.crypto_trader.api_server.domain.entities.Candle;
import com.crypto_trader.api_server.infra.CandleMongoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CandleController {

    private final ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate;

    @Autowired
    public CandleController(ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate) {
        this.byteArrayRedisTemplate = byteArrayRedisTemplate;
    }

//    @GetMapping("/api/all-candles")
//    public List<CandleResponseDto> getAllCandles(@RequestParam String market) {
//        // MongoDB에서 해당 market에 대한 모든 candle 가져오기
//        List<Candle> candles = candleMongoRepository.findCandlesByMarket(market);
//
//        // Candle 엔티티를 CandleResponseDto로 변환
//        List<CandleResponseDto> response = candles.stream()
//                .map(CandleResponseDto::new)
//                .collect(Collectors.toList());
//
//
//        System.out.println("response count?" + response.stream().count());
//        return response;
//    }

    // using protobuf
    @GetMapping("/api/all-candles")
    public Mono<List<byte[]>> getAllCandlesP(@RequestParam CandleRequestDto dto) {
        return byteArrayRedisTemplate.opsForList()
                .range(dto.makeKey(), 0, -1)
                .collectList();
    }
}
