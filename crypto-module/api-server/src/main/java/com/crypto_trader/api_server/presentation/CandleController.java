package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.dto.CandleRangeRequestDto;
import com.crypto_trader.api_server.dto.CandleRequestDto;
import com.crypto_trader.api_server.domain.CandleUnit;
import com.crypto_trader.api_server.dto.CandlesInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class CandleController {

    private final ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate;

    @Autowired
    public CandleController(ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate) {
        this.byteArrayRedisTemplate = byteArrayRedisTemplate;
    }

    // using protobuf
    @GetMapping("/api/all-candles")
    public Mono<List<byte[]>> getAllCandlesP(@ModelAttribute CandleRequestDto dto) {
        String key = dto.makeKey();
        return byteArrayRedisTemplate.opsForList()
                .range(key, 0, -1)
                .collectList();
    }

    @GetMapping("/api/candles-info")
    public Mono<CandlesInfo> getCandlesInfo(@ModelAttribute CandleRequestDto dto) {
        return byteArrayRedisTemplate.opsForList()
                .size(dto.makeKey())
                .flatMap(count -> byteArrayRedisTemplate.opsForList()
                        .range(dto.makeKey(), -100, -1)
                        .collectList()
                        .map(bytes -> new CandlesInfo(
                                -100,
                                -1,
                                count.intValue(),
                                bytes
                        )));
    }

    @GetMapping("/api/candles")
    public Mono<List<byte[]>> getCandles(@ModelAttribute CandleRangeRequestDto dto) {
        return byteArrayRedisTemplate.opsForList()
                .range(dto.makeKey(), dto.getStart(), dto.getEnd())
                .collectList();
    }
}
