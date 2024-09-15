package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.dto.CandleResponseDto;
import com.crypto_trader.api_server.domain.entities.Candle;
import com.crypto_trader.api_server.infra.CandleMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CandleController {


    private final CandleMongoRepository candleMongoRepository;

    public CandleController(CandleMongoRepository candleMongoRepository) {
        this.candleMongoRepository = candleMongoRepository;
    }

    @GetMapping("/api/all-candles")
    public List<CandleResponseDto> getAllCandles(@RequestParam String market) {
        // MongoDB에서 해당 market에 대한 모든 candle 가져오기
        List<Candle> candles = candleMongoRepository.findCandlesByMarket(market);

        // Candle 엔티티를 CandleResponseDto로 변환
        List<CandleResponseDto> response = candles.stream()
                .map(CandleResponseDto::new)
                .collect(Collectors.toList());

        return response;
    }
}
