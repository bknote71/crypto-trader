package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.domain.OrderSide;
import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.entities.Order;
import com.crypto_trader.api_server.infra.OrderRepository;
import com.crypto_trader.api_server.infra.SimpleMarketRepository;
import com.crypto_trader.api_server.infra.TickerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderExecutionService {

    private final OrderRepository orderRepository;
    private final TickerRepository tickerRepository;
    private final SimpleMarketRepository marketRepository;
    private final ObjectMapper objectMapper;

    private final Map<String, Sinks.Many<Ticker>> sinkMap = new HashMap<>();
    private final Map<String, Disposable> subscriptionMap = new HashMap<>();


    @Autowired
    public OrderExecutionService(OrderRepository orderRepository,
                                 TickerRepository tickerRepository,
                                 SimpleMarketRepository marketRepository,
                                 ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.tickerRepository = tickerRepository;
        this.marketRepository = marketRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        tickerRepository.getChannel()
                .subscribe(value -> {
                    try {
                        Ticker ticker = objectMapper.readValue(value.getMessage(), Ticker.class);
                        tickerRepository.save(ticker);

                        Sinks.Many<Ticker> sink = sinkMap.get(ticker.getCode());
                        if (sink == null) {
                            throw new RuntimeException();
                        }

                        sink.tryEmitNext(ticker).orThrow();

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        marketRepository.marketCodesUpdates()
                .subscribe(codes -> {
                    subscriptionMap.values().forEach(Disposable::dispose);
                    subscriptionMap.clear();
                    sinkMap.clear();

                    for (String code : codes) {
                        Sinks.Many<Ticker> sink = Sinks.many().unicast().onBackpressureBuffer();
                        sinkMap.put(code, sink);

                        Disposable subscription = sink.asFlux()
                                .sampleFirst(Duration.ofSeconds(1)) // 1초에 첫 번째만 받아서 처리 (sample은 마지막 값을 처리)
                                .subscribe(this::processTicker);

                        subscriptionMap.put(code, subscription);
                    }
                });
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    protected void processTicker(Ticker ticker) {
        String market = ticker.getCode();
        double tradePrice = ticker.getTradePrice();
        // 주문 체결 수행
        orderRepository.findByMarket(market).stream()
                .filter(order -> {
                    double price = order.getPrice().doubleValue();
                    return (order.getSide() == OrderSide.BID) ? tradePrice <= price : tradePrice >= price;
                })
                .forEach(Order::execution);
    }
}
