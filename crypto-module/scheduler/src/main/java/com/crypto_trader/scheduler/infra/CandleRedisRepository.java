package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.config.redis.ReactiveRedisPubSubTemplate;
import com.crypto_trader.scheduler.domain.CandleState;
import com.crypto_trader.scheduler.domain.CandleUnit;
import com.crypto_trader.scheduler.proto.DataModel;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.crypto_trader.scheduler.global.constant.RedisConst.MINUTE_CANDLE;
import static com.crypto_trader.scheduler.proto.DataModel.*;

@Slf4j
@Repository
public class CandleRedisRepository {

    private final ReactiveRedisPubSubTemplate<byte[]> pubSubTemplate;

    @Autowired
    public CandleRedisRepository(ReactiveRedisPubSubTemplate<byte[]> pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void saveMinuteCandle(String market, CandleState candleState, CandleUnit unit) {
        String key = unit + MINUTE_CANDLE + market;

        PCandle pCandle = new PCandle.Builder()
                .setOpen(candleState.getOpen())
                .setClose(candleState.getLast())
                .setHigh(candleState.getHigh())
                .setLow(candleState.getLow())
                .setVolume(candleState.getVolume())
                .setTime(candleState.getTime().toString())
                .build();

        pubSubTemplate
                .master
                .opsForList()
                .rightPush(key, pCandle.toByteArray())  // 데이터를 리스트에 추가
                .doOnError(error -> {log.debug(error.getMessage());})
                .subscribe();
    }

    public Mono<List<CandleState>> getOneMinuteCandle(String market) {
        String key = CandleUnit.ONEMINUTE + MINUTE_CANDLE + market;
        return pubSubTemplate
                .master
                .opsForList()
                .range(key, 0, -1)
                .<CandleState>handle((bytes, sink) -> {
                    try {
                        PCandle candle = PCandle.parseFrom(bytes);
                        CandleState candleState = new CandleState(
                                candle.getOpen(),
                                candle.getClose(),
                                candle.getHigh(),
                                candle.getLow(),
                                candle.getVolume()
                        );
                        sink.next(candleState);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collectList();
    }
}
