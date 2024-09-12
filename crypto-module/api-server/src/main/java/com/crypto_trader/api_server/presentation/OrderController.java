package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.OrderService;
import com.crypto_trader.api_server.application.dto.OrderCancelRequestDto;
import com.crypto_trader.api_server.application.dto.OrderCreateRequestDto;
import com.crypto_trader.api_server.application.dto.OrderResponseDto;
import com.crypto_trader.api_server.auth.PrincipalUser;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public OrderResponseDto createOrder(@AuthenticationPrincipal PrincipalUser principalUser,
                                        @RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        assert principalUser.getUser() != null;
        return orderService.createOrder(principalUser, orderCreateRequestDto);
    }

    @PatchMapping("cancel")
    public OrderResponseDto cancelOrder(@AuthenticationPrincipal PrincipalUser principalUser,
                                        @RequestBody OrderCancelRequestDto orderCancelRequestDto) {
        assert principalUser.getUser() != null;
        return orderService.cancelOrder(principalUser, orderCancelRequestDto);
    }
}
