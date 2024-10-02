package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.domain.entities.Order;
import com.crypto_trader.api_server.infra.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessOrderExecution {
    private final OrderRepository orderRepository;

    @Autowired
    public ProcessOrderExecution(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public boolean process(String market, double tradePrice, PageRequest pageable) {
        Page<Order> orderChunk = orderRepository.findByMarketWithPrice(market, tradePrice, pageable);
        // 기본 parallelStream: ForkJoinPool.commonPool() 사용
        // - 시스템의 가용 CPU 코어 수에 따라 설정

        // 스레드 수를 직접 조정하려면 별도의 ForkJoinPool 사용
        List<Order> ordersToExecute = new ArrayList<>(orderChunk.getContent());
        ordersToExecute.parallelStream().forEach(Order::execution);
        return orderChunk.isLast();
    }
}
