package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.LatestCandleDatas;
import com.crypto_trader.api_server.application.LatestCandleService;
import com.crypto_trader.api_server.dto.CandleRangeRequestDto;
import com.crypto_trader.api_server.dto.CandleRequestDto;
import com.crypto_trader.api_server.dto.CandlesInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveListCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class CandleController {

    private final LatestCandleService latestCandleService;
    private final ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate;

    @Autowired
    public CandleController(LatestCandleService latestCandleService,
                            ReactiveRedisTemplate<String, byte[]> byteArrayRedisTemplate) {
        this.latestCandleService = latestCandleService;
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
    public Flux<CandlesInfo> getCandlesInfo(@ModelAttribute CandleRequestDto dto) {

        LatestCandleDatas lastCandleDatas = latestCandleService.getLatestCandle(dto.getMarket());
        if (lastCandleDatas != null && lastCandleDatas.getCount() >= 100) {
            return Flux.just(new CandlesInfo(
                    -100,
                    -1,
                    (int) lastCandleDatas.getCount(),
                    lastCandleDatas.datas()
            ));
        }

        ByteBuffer redisKey = ByteBuffer.wrap(dto.makeKey().getBytes(StandardCharsets.UTF_8));
        return byteArrayRedisTemplate.execute(connection -> {
                    ReactiveListCommands listCommands = connection.listCommands();
                    Mono<Long> sizeMono = listCommands.lLen(redisKey);
                    Mono<List<byte[]>> listMono = listCommands.lRange(redisKey, -100, -1)
                            .map(ByteBuffer::array)
                            .collectList();

                    return Mono.zip(sizeMono, listMono);
                })
                .map(tuple -> {
                    Long count = tuple.getT1();
                    List<byte[]> bytes = tuple.getT2();
                    return new CandlesInfo(
                            -100,
                            -1,
                            count.intValue(),
                            bytes
                    );
                })
                .onErrorResume(e -> Mono.just(new CandlesInfo(-100, -1, 0, List.of())));
//
        // 1만 바이트 (비교군)
//        List<CandlesInfo> infos = new ArrayList<>();
//        for (int i = 0; i < 230; ++i) {
//            infos.add(new CandlesInfo());
//        }
//
//        return Flux.just(infos.toArray(new CandlesInfo[0]));

//        return byteArrayRedisTemplate.opsForList()
//                .size(dto.makeKey())
//                .flatMap(count -> byteArrayRedisTemplate.opsForList()
//                        .range(dto.makeKey(), -100, -1)
//                        .collectList()
//                        .map(bytes -> new CandlesInfo(
//                                -100,
//                                -1,
//                                count.intValue(),
//                                bytes
//                        )));
    }

    @GetMapping("/api/candles")
    public Mono<List<byte[]>> getCandles(@ModelAttribute CandleRangeRequestDto dto) {
        return byteArrayRedisTemplate.opsForList()
                .range(dto.makeKey(), dto.getStart(), dto.getEnd())
                .collectList();
    }
}
