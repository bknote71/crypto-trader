package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.domain.OrderSide;
import com.crypto_trader.api_server.domain.Ticker;
import com.crypto_trader.api_server.domain.entities.Order;
import com.crypto_trader.api_server.domain.entities.OrderState;
import com.crypto_trader.api_server.domain.events.TickerProcessingEvent;
import com.crypto_trader.api_server.infra.OrderRepository;
import com.crypto_trader.api_server.infra.SimpleMarketRepository;
import com.crypto_trader.api_server.infra.TickerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderExecutionService {

    private final OrderService orderService;
    private final TickerRepository tickerRepository;
    private final SimpleMarketRepository marketRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    private final Map<String, Sinks.Many<Ticker>> sinkMap = new HashMap<>();
    private final Map<String, Disposable> subscriptionMap = new HashMap<>();

    @Autowired
    public OrderExecutionService(OrderService orderService,
                                 TickerRepository tickerRepository,
                                 SimpleMarketRepository marketRepository,
                                 ObjectMapper objectMapper,
                                 ApplicationEventPublisher eventPublisher) {
        this.orderService = orderService;
        this.tickerRepository = tickerRepository;
        this.marketRepository = marketRepository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        subscribeToMarketCodes();
        subscribeToTickerChannel();
    }

    /**
     * 시장 코드에 대한 구독을 처리하고, 새로운 시장 코드에 따라 Sink 및 구독을 설정한다.
     */
    private void subscribeToMarketCodes() {
        marketRepository.marketCodesUpdates()
                .subscribe(codes -> {
                    clearSubscriptionsAndSinks();
                    initializeSinksAndSubscriptions(codes);
                });
    }

    /**
     * Redis Ticker 채널에 구독하여 실시간으로 Ticker 데이터를 받아서 처리한다.
     */
    private void subscribeToTickerChannel() {
        tickerRepository.getChannel()
                .subscribe(value -> processIncomingTickerMessage(value.getMessage()));
    }

    /**
     * 기존의 구독과 Sink를 정리한다.
     */
    private void clearSubscriptionsAndSinks() {
        subscriptionMap.values().forEach(Disposable::dispose);
        subscriptionMap.clear();
        sinkMap.clear();
    }

    /**
     * 새로운 시장 코드 리스트에 따라 각 시장에 대한 Sink 및 구독을 설정한다.
     *
     * @param codes 시장 코드 리스트
     */
    private void initializeSinksAndSubscriptions(List<String> codes) {
        for (String code : codes) {
            Sinks.Many<Ticker> sink = createSinkForMarket();
            sinkMap.put(code, sink);

            Disposable subscription = createSubscriptionForMarket(sink, code);
            subscriptionMap.put(code, subscription);
        }
    }

    /**
     * 특정 시장에 대한 Sink를 생성한다.
     *
     * @return 생성된 Sink
     */
    private Sinks.Many<Ticker> createSinkForMarket() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    /**
     * 특정 시장에 대한 구독을 설정한다. 이 구독은 1초에 한 번씩만 데이터를 처리한다.
     *
     * @param sink  시장에 대한 Sink
     * @param code  시장 코드
     * @return 생성된 구독
     */
    private Disposable createSubscriptionForMarket(Sinks.Many<Ticker> sink, String code) {
        return sink.asFlux()
                .sampleFirst(Duration.ofSeconds(1)) // 1초에 한 번씩 처리
                .subscribe(ticker -> eventPublisher.publishEvent(new TickerProcessingEvent(this, ticker)));
    }

    /**
     * Redis에서 받아온 Ticker 메시지를 처리하여 적절한 Sink에 발행한다.
     *
     * @param message Ticker 메시지
     */
    private void processIncomingTickerMessage(String message) {
        try {
            Ticker ticker = objectMapper.readValue(message, Ticker.class);
            tickerRepository.save(ticker);

            Sinks.Many<Ticker> sink = sinkMap.get(ticker.getMarket());
            if (sink == null) {
                throw new RuntimeException("Sink not found for market: " + ticker.getMarket());
            }

            sink.tryEmitNext(ticker).orThrow();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process Ticker message", e);
        }
    }

    @EventListener
    public void processTicker(TickerProcessingEvent event) {
        Ticker ticker = event.getTicker();
        String market = ticker.getMarket();
        double tradePrice = ticker.getTradePrice();

        // 1. 비동기적으로 해당 시장의 주문들을 먼저 조회
        List<Order> ordersToProcess = orderService.getOrderToProcess(market, tradePrice);

        // 2. 락을 개별 주문 실행에만 적용
        ordersToProcess.forEach(orderService::processOrderWithLock);
    }
}