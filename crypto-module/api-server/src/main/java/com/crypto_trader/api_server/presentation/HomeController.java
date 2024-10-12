package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.SimpleMarketService;
import com.crypto_trader.api_server.application.TickerService;
import com.crypto_trader.api_server.dto.CryptoDto;
import com.crypto_trader.api_server.dto.TickerResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class HomeController {

    private final SimpleMarketService marketService;
    private final TickerService tickerService;

    public HomeController(SimpleMarketService marketService, TickerService tickerService) {
        this.marketService = marketService;
        this.tickerService = tickerService;
    }

    @GetMapping("/")
    public String home() {
        System.out.println("Home page");
        return "home";
    }

    @GetMapping("/api/cryptos")
    public Mono<List<CryptoDto>> getAllCryptos() {
        return marketService.getAllCryptos();
    }

    @GetMapping("/api/tickers")
    public List<TickerResponseDto> getAllTickers() {
        return tickerService.getTickers();
    }
}
