package com.crypto_trader.api_server.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api/*")
    public String home() {
        return "api home!";
    }

    @GetMapping("home")
    public String homePage() {
        return "this is home!";
    }
}
