package com.crypto_trader.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final WebSocketServerManager webSocketServerManager;

    @Autowired
    public ApiController(WebSocketServerManager webSocketServerManager) {
        this.webSocketServerManager = webSocketServerManager;
    }

    @GetMapping("/best-websocket-server")
    public String getBestWebSocketServer() {
        return webSocketServerManager.getBestServer();
    }
}
