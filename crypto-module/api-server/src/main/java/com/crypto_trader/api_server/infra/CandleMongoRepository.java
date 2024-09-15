package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.domain.entities.Candle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CandleMongoRepository {

    private final MongoTemplate mongoTemplate;

    public CandleMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Candle> findCandlesByMarket(String market) {
        Query query = new Query(Criteria.where("market").is(market));
        return mongoTemplate.find(query, Candle.class);
    }
}
