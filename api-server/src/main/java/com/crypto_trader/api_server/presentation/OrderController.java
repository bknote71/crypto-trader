package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.OrderService;
import com.crypto_trader.api_server.application.dto.OrderCancelRequestDto;
import com.crypto_trader.api_server.application.dto.OrderCreateRequestDto;
import com.crypto_trader.api_server.application.dto.OrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public OrderResponseDto createOrder(@RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        return orderService.createOrder(null, orderCreateRequestDto);
    }

    @PatchMapping("cancel")
    public OrderResponseDto cancelOrder(@RequestBody OrderCancelRequestDto orderCancelRequestDto) {
        return orderService.cancelOrder(null, orderCancelRequestDto);
    }
}
