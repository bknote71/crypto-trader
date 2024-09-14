package com.crypto_trader.scheduler.infra;

import com.crypto_trader.scheduler.domain.entity.Candle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CandleMongoRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CandleMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void saveAll(final List<Candle> candles) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Candle.class);
        bulkOps.insert(candles);
        bulkOps.execute();
    }

    public List<Candle> findAllCandles() {
        return mongoTemplate.findAll(Candle.class);
    }

    public List<Candle> findCandlesByMarket(String market) {
        Query query = new Query(Criteria.where("market").is(market));
        return mongoTemplate.find(query, Candle.class);
    }

    public List<Candle> findCandlesByMarketAndTime(String market, LocalDateTime startTime) {
        Query query = new Query(Criteria.where("market").is(market).and("time").lt(startTime));
//        query.limit(100);  // 필요에 따라 조회할 캔들의 개수 제한 가능
        return mongoTemplate.find(query, Candle.class);
    }
}
