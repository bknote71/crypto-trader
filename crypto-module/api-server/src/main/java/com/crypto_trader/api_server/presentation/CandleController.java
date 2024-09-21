package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.dto.CandleResponseDto;
import com.crypto_trader.api_server.domain.entities.Candle;
import com.crypto_trader.api_server.infra.CandleMongoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final CandleMongoRepository candleMongoRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CandleController(CandleMongoRepository candleMongoRepository, ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.candleMongoRepository = candleMongoRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/all-candles")
    public List<CandleResponseDto> getAllCandles(@RequestParam String market) {
        // MongoDB에서 해당 market에 대한 모든 candle 가져오기
        List<Candle> candles = candleMongoRepository.findCandlesByMarket(market);

        // Candle 엔티티를 CandleResponseDto로 변환
        List<CandleResponseDto> response = candles.stream()
                .map(CandleResponseDto::new)
                .collect(Collectors.toList());


        System.out.println("response count?" + response.stream().count());
        return response;
    }

    @GetMapping("/api/all-candles-r")
    public Mono<List<CandleResponseDto>> getAllCandlesR(@RequestParam String market) {
        String key = "ONEMINUTE:minute_candle:" + market;
        return redisTemplate.opsForList()
                .range(key, 0, -1)
                .flatMap(candle -> {
                    try {
                        return Mono.just(objectMapper.readValue(candle, CandleResponseDto.class));
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                })
                .collectList();
    }

    // using protobuf


}
