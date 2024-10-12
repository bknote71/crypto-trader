package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.dto.TickerResponseDto;
import com.crypto_trader.api_server.infra.TickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TickerService {

    private final TickerRepository tickerRepository;

    public TickerService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    public List<TickerResponseDto> getTickers() {
         return tickerRepository.findAllTickers().stream()
                 .map(TickerResponseDto::from)
                 .toList();
    }
}
