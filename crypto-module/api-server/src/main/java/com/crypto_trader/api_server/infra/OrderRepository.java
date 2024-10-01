package com.crypto_trader.api_server.infra;

import com.crypto_trader.api_server.domain.entities.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.user u " +
            "LEFT JOIN FETCH u.assets a " + // left가 있어야 user의 assets가 없어도 user가 조회 됨
            "WHERE o.market = :market")
    List<Order> findByMarket(@Param("market") String market);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Orders o " +
            "JOIN FETCH o.user u " +
            "LEFT JOIN FETCH u.assets a " +
            "WHERE o.market = :market " +
            "AND o.state = 'CREATED' " +
            "AND ((o.side = 'BID' AND o.price >= :tradePrice) OR (o.side = 'ASK' AND o.price <= :tradePrice))")
    Page<Order> findByMarketWithPrice(@Param("market") String market,
                                      @Param("tradePrice") Number tradePrice,
                                      Pageable pageable);
}
