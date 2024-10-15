package com.crypto_trader.monitor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class WebSocketServerManager {

    private final RestTemplate restTemplate;
    private final DockerService dockerService;

    private final List<String> webSocketServers = new ArrayList<>();

    private static final int WEBSOCKET_THRESHOLD = 10;  // 임계치 설정
    private static final double SCALE_OUT_RATIO = 0.5;
    public static final String apiServerImage = "bknote71/api-server:latest";

    public WebSocketServerManager(RestTemplate restTemplate,
                                  DockerService dockerService) {
        this.restTemplate = restTemplate;
        this.dockerService = dockerService;
    }

    public List<String> getWebSocketServers() {
        return webSocketServers;
    }

    public double getWebSocketSessionCount(String serverUrl) {
        String metricUrl = serverUrl + "/actuator/metrics/websocket.ticker.sessions.count";
        try {
            Double sessionCount = restTemplate.getForObject(metricUrl, Double.class);
            return sessionCount != null ? sessionCount : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean isOverloaded(String serverUrl) {
        return getWebSocketSessionCount(serverUrl) > WEBSOCKET_THRESHOLD;
    }

    public String getBestServer() {
        // TODO: 조금 더 복잡한 조건
        return webSocketServers.stream()
                .min(Comparator.comparingDouble(this::getWebSocketSessionCount))
                .orElseThrow(() -> new RuntimeException("No Available WebSocket Servers"));
    }

    public void scaleOutIfNeeded() {
        long overloadedCount = webSocketServers.stream()
                .filter(this::isOverloaded)
                .count();

        if (overloadedCount >= webSocketServers.size() * SCALE_OUT_RATIO) {
            try {
                String newServerUrl = dockerService.scaleOut(apiServerImage, "websocket-server-" + System.currentTimeMillis());
                webSocketServers.add(newServerUrl);
                System.out.println("New WebSocket server added: " + newServerUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}