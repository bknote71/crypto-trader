package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.SimpleMarketService;
import com.crypto_trader.api_server.application.dto.CryptoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    private final SimpleMarketService marketService;

    @Autowired
    public HomeController(SimpleMarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/api/cryptos")
    public List<CryptoDto> getAllCryptos() {
        return marketService.getAllCryptos();
    }

    @GetMapping("/api/*")
    public String home() {
        return "api home!";
    }

    @GetMapping("home")
    public String homePage() {
        return "this is home!";
    }
}
