package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.dto.CryptoDto;
import com.crypto_trader.api_server.infra.SimpleMarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimpleMarketService {

    private final SimpleMarketRepository marketRepository;

    @Autowired
    public SimpleMarketService(SimpleMarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    public Mono<List<CryptoDto>> getAllCryptos() {
        return marketRepository.marketCodesUpdates()
                .flatMapIterable(list -> list)
                .map(CryptoDto::new)
                .collectList();
    }
}
