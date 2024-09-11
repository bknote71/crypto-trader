package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.application.dto.TickerRequestDto;
import com.crypto_trader.api_server.infra.TickerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TickerService {

    private final TickerRepository tickerRepository;

    public TickerService(TickerRepository tickerRepository) {
        this.tickerRepository = tickerRepository;
    }

    public List<TickerRequestDto> getTickers() {
         return tickerRepository.findAllTickers().stream()
                 .map(TickerRequestDto::from)
                 .toList();
    }
}
