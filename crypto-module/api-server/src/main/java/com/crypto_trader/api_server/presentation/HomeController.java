package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.SimpleMarketService;
import com.crypto_trader.api_server.application.TickerService;
import com.crypto_trader.api_server.application.dto.CryptoDto;
import com.crypto_trader.api_server.application.dto.TickerRequestDto;
import com.crypto_trader.api_server.domain.Ticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    private final SimpleMarketService marketService;
    private final TickerService tickerService;

    public HomeController(SimpleMarketService marketService, TickerService tickerService) {
        this.marketService = marketService;
        this.tickerService = tickerService;
    }

    @GetMapping("/api/cryptos")
    public List<CryptoDto> getAllCryptos() {
        return marketService.getAllCryptos();
    }

    @GetMapping("/api/tickers")
    public List<TickerRequestDto> getAllTickers() {
        return tickerService.getTickers();
    }
}
