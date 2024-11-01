package com.crypto_trader.monitor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class WebSocketServerManager {

    private final RestTemplate restTemplate;
    private final DockerService dockerService;

    private final List<String> webSocketServers = new ArrayList<>();

    private static final int WEBSOCKET_THRESHOLD = 10;  // 임계치 설정
    private static final double MEM_THRESHOLD = 100_000_000;
    private static final double SCALE_OUT_RATIO = 0.7;
    public static final String apiServerImage = "bknote71/api-server:latest";

    public WebSocketServerManager(RestTemplate restTemplate,
                                  DockerService dockerService) {
        this.restTemplate = restTemplate;
        this.dockerService = dockerService;
    }

    public int getWebSocketSessionCount(String serverIp) {
        Object measurementsValue = getMeasurementsValue(serverIp, "/actuator/metrics/websocket.ticker.sessions.count");
        if (measurementsValue != null) {
            return (int) measurementsValue;
        }

        return 0;
    }

    /**
     *
     * @param serverIp
     * @return
     * - if not stopped
     */
    public boolean stopOOMInstance(String serverIp) {
        Object measurementsValue = getMeasurementsValue(serverIp, "/actuator/metrics/application.oom.status");
        if (measurementsValue == null) {
            return true;
        }

        boolean oomOccurred = (int) measurementsValue == 1;

        if (!oomOccurred)
            return true;

        dockerService.scaleIn(serverIp);
        return false;
    }

    private double getAvailableMem(String serverIp) {
        Object maxMemory = getMeasurementsValue(serverIp, "/actuator/metrics/jvm.memory.max");
        if (maxMemory == null) {
            return 0;
        }

        Object usedMemory = getMeasurementsValue(serverIp, "/actuator/metrics/jvm.memory.used");
        if (usedMemory == null) {
            return 0;
        }

        return (double) maxMemory - (double) usedMemory;
    }

    private Object getMeasurementsValue(String serverIp, String metricPath) {
        String metricUrl = "http://" + serverIp + ":" + 8090 + metricPath;
        try {
            Map<String, Object> response = restTemplate.getForObject(metricUrl, Map.class);

            if (response != null && response.get("measurements") != null) {
                List<Map<String, Object>> measurements = (List<Map<String, Object>>) response.get("measurements");
                if (!measurements.isEmpty()) {
                    return measurements.get(0).get("value");
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean isOverloaded(String serverIp) {
        return getWebSocketSessionCount(serverIp) > WEBSOCKET_THRESHOLD ||
                getAvailableMem(serverIp) < MEM_THRESHOLD;
    }

    public String getBestServer() {
        // TODO: 조금 더 엄밀한 조건
        return webSocketServers.stream()
                .min(Comparator.comparingInt(this::getWebSocketSessionCount))
                .orElseThrow(() -> new RuntimeException("No Available WebSocket Servers"));
    }

    public void scaleOutIfNeeded() {
        long overloadedCount = webSocketServers.parallelStream()
                .filter(this::stopOOMInstance)
                .filter(this::isOverloaded)
                .count();

        // TODO: minimum size requirements

        if (overloadedCount >= webSocketServers.size() * SCALE_OUT_RATIO) {
            try {
                String newServerIp = dockerService.scaleOut(apiServerImage, "websocket-server-" + System.currentTimeMillis());
                if (newServerIp != null) {
                    webSocketServers.add(newServerIp);
                    System.out.println("New WebSocket server added: " + newServerIp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}