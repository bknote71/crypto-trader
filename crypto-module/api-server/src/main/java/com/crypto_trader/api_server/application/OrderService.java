package com.crypto_trader.api_server.application;

import com.crypto_trader.api_server.application.dto.OrderCancelRequestDto;
import com.crypto_trader.api_server.auth.PrincipalUser;
import com.crypto_trader.api_server.domain.entities.Order;
import com.crypto_trader.api_server.domain.entities.OrderState;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import com.crypto_trader.api_server.application.dto.OrderCreateRequestDto;
import com.crypto_trader.api_server.application.dto.OrderResponseDto;
import com.crypto_trader.api_server.infra.OrderRepository;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(PrincipalUser principalUser, OrderCreateRequestDto orderCreateRequestDto) {
        UserEntity user = principalUser.getUser();

        Order order = orderCreateRequestDto.toEntity();
        order.validationWith(user);

        user.getAccount().lock(order.totalPrice());
        orderRepository.save(order);

        return OrderResponseDto.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders(PrincipalUser principalUser) {
        UserEntity user = principalUser.getUser();

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(OrderResponseDto::toDto)
                .toList();
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public OrderResponseDto cancelOrder(PrincipalUser principalUser, OrderCancelRequestDto dto) {
        UserEntity user = principalUser.getUser();

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!Objects.equals(order.getUser().getId(), user.getId()))
            throw new IllegalStateException("User id does not match");

//        if (order.getState() != OrderState.CREATED)
//            throw new IllegalStateException("Order can't be cancelled");

        order.cancel(dto.getMarket());

        // remove ??
        // orderRepository.delete(order);

        return new OrderResponseDto();
    }
}
