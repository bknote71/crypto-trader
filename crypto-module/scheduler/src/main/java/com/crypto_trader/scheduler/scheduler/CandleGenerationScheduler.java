package com.crypto_trader.scheduler.scheduler;

import com.crypto_trader.scheduler.domain.entity.Candle;
import com.crypto_trader.scheduler.domain.CandleState;
import com.crypto_trader.scheduler.domain.CandleUnit;
import com.crypto_trader.scheduler.infra.CandleMongoRepository;
import com.crypto_trader.scheduler.infra.CandleRedisRepository;
import com.crypto_trader.scheduler.infra.SimpleCandleRepository;
import com.crypto_trader.scheduler.infra.SimpleMarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CandleGenerationScheduler {

    private final SimpleCandleRepository simpleCandleRepository;
    private final CandleMongoRepository candleMongoRepository;
    private final CandleRedisRepository candleRedisRepository;
    private final SimpleMarketRepository marketRepository;

    @Autowired
    public CandleGenerationScheduler(SimpleCandleRepository simpleCandleRepository,
                                     CandleMongoRepository candleMongoRepository,
                                     CandleRedisRepository candleRedisRepository,
                                     SimpleMarketRepository marketRepository) {
        this.simpleCandleRepository = simpleCandleRepository;
        this.candleMongoRepository = candleMongoRepository;
        this.candleRedisRepository = candleRedisRepository;
        this.marketRepository = marketRepository;
    }

    @Scheduled(cron = "*/3 * * * * *")
    private void generateOneMinuteCandle() {
        Map<String, CandleState> candleStates = simpleCandleRepository.getCandleStates();
        if (candleStates.isEmpty())
            return;

        List<Candle> candles = candleStates.entrySet().stream()
                .map(entry -> {
                    String market = entry.getKey();
                    CandleState candleState = entry.getValue();
                    return new Candle(
                            market,
                            candleState.getOpen(),
                            candleState.getLast(),
                            candleState.getHigh(),
                            candleState.getLow(),
                            candleState.getVolume()
                    );
                })
                .toList();

        candleMongoRepository.saveAll(candles);
        candleStates.forEach((market, candleState) -> candleRedisRepository.saveMinuteCandle(market, candleState, CandleUnit.ONEMINUTE));
        candleStates.values().forEach(CandleState::reset); // 후처리


    }

    @Scheduled(cron = "0 */5 * * * *")
    public void generateFiveMinuteCandles() {
//        aggregateAndStoreCandles(CandleUnit.FIVEMINUTE);
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void generateTenMinuteCandles() {
//        aggregateAndStoreCandles(CandleUnit.TENMINUTE);
    }

    private void aggregateAndStoreCandles(CandleUnit unit) {
        List<String> allMarketCodes = marketRepository.getAllMarketCodes();
        allMarketCodes.forEach(market ->
                candleRedisRepository.getOneMinuteCandle(market)
                        .subscribe(candleStates -> {
                            CandleState newCandleState = generateNewCandleState(candleStates, unit);
                            if (newCandleState == null)
                                return;

                            candleRedisRepository.saveMinuteCandle(market, newCandleState, unit);
                })
        );
    }

    private CandleState generateNewCandleState(List<CandleState> candleStates, CandleUnit candleUnit) {
        int num = candleUnit.num;
        int candlesSize = candleStates.size();

        if (candlesSize < num)
            return null;

        return CandleState.aggregate(candleStates.subList(candlesSize - num, candlesSize));
    }
}
