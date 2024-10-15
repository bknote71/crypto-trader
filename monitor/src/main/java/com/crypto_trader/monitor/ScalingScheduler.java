package com.crypto_trader.monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScalingScheduler {

    private final WebSocketServerManager webSocketServerManager;

    @Autowired
    public ScalingScheduler(WebSocketServerManager webSocketServerManager) {
        this.webSocketServerManager = webSocketServerManager;
    }

    // 10초마다 스케일 아웃 체크
    @Scheduled(fixedRate = 10000)
    public void checkAndScaleOut() {
        webSocketServerManager.scaleOutIfNeeded();
    }
}
