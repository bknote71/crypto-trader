package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long id);

    // TODO: fetch join
    List<Order> findByMarket(String market);
}
