package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.domain.CandleState;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SimpleCandleRepository {

    // private state
    private final Map<String, CandleState> candleStates = new ConcurrentHashMap<>();

    public Map<String, CandleState> getCandleStates() {
        return candleStates;
    }

    public void update(String market, double value) {
        CandleState candleState = candleStates.get(market);

        if (candleState == null) {
            candleStates.put(market, new CandleState(value));
            return;
        }

        candleState.update(value);
    }

    public void saveAllState() {
        if (candleStates.isEmpty())
            return;
    }
}
