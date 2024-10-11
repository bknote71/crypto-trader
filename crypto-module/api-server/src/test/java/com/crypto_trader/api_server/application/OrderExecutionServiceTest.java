//package com.crypto_trader.api_server.application;
//
//import com.crypto_trader.api_server.application.dto.OrderCancelRequestDto;
//import com.crypto_trader.api_server.auth.PrincipalUser;
//import com.crypto_trader.api_server.domain.OrderSide;
//import com.crypto_trader.api_server.domain.entities.Account;
//import com.crypto_trader.api_server.domain.entities.Order;
//import com.crypto_trader.api_server.domain.entities.UserEntity;
//import com.crypto_trader.api_server.infra.OrderRepository;
//import com.crypto_trader.api_server.infra.UserEntityRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class OrderExecutionServiceTest {
//
//    @Autowired private UserEntityRepository userEntityRepository;
//    @Autowired private OrderRepository orderRepository;
//    @Autowired private OrderExecutionService orderExecutionService;
//    @Autowired private OrderService orderService;
//
//    PrincipalUser principalUser;
//    UserEntity user;
//    List<Order> orders = new ArrayList<>();
//
//    @BeforeEach
//    void setUp() {
//        user = new UserEntity("user");
//        userEntityRepository.save(user);
//
//        principalUser = new PrincipalUser(user);
//
//        // first init
//        for (int i = 0; i < 200_000; ++i) {
//            Order order = Order.builder()
//                    .market("KRW-BTC")
//                    .side(OrderSide.BID)
//                    .volume(1)
//                    .price(100000000)
//                    .build();
//            order.validationWith(user);
//            orders.add(order);
//        }
//
//        orderRepository.saveAll(orders);
//    }
//
//    @Test
//    void oldProcessOrderExecution() {
//        ExecutorService es = Executors.newFixedThreadPool(2);
//
//        Callable<Void> task1 = () -> {
//            orderExecutionService.oldProcessOrderExecution("KRW-BTC", 99999999);
//            return null;
//        };
//
//        Callable<Void> task2 = () -> {
//            Thread.sleep(1000);
//            for (int i = 0; i < 1_000_000; ++i) { // 1백만명이 동시에 했다고 가정
//                Long id = orders.get(i).getId();
//                orderService.cancelOrder(
//                        principalUser,
//                        new OrderCancelRequestDto("KRW-BTC", id)
//                );
//                if (i == 0) {
//                    System.out.println("order cancel start");
//                }
//                if (i % 100_000 == 0) {
//                    System.out.println("order cancel completed");
//                }
//            }
//            return null;
//        };
//
//        Future<Void> f1 = es.submit(task1);
//        Future<Void> f2 = es.submit(task2);
//
//        es.shutdown();
//
//        try {
//            f1.get(); // 첫 번째 작업이 완료될 때까지 대기
//            f2.get(); // 두 번째 작업이 완료될 때까지 대기
//        } catch (Exception  e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    void newProcessOrderExecution() {
//        ExecutorService es = Executors.newFixedThreadPool(2);
//
//        Callable<Void> task1 = () -> {
//            orderExecutionService.processOrderExecution("KRW-BTC", 99999999);
//            return null;
//        };
//
//        Callable<Void> task2 = () -> {
//            long start = System.currentTimeMillis();
//            Thread.sleep(1000);
//            for (int i = 0; i < 200_000; ++i) { // 1백만명이 동시에 했다고 가정
//                Long id = orders.get(i).getId();
//                orderService.cancelOrder(
//                        principalUser,
//                        new OrderCancelRequestDto("KRW-BTC", id)
//                );
//                if (i == 0) {
//                    System.out.println("order cancel start " + (System.currentTimeMillis() - start));
//                }
//                if (i % 20_000 == 0) {
//                    System.out.println("order cancel completed " + (System.currentTimeMillis() - start));
//                }
//            }
//            return null;
//        };
//
//        Future<Void> f1 = es.submit(task1);
//        Future<Void> f2 = es.submit(task2);
//
//        es.shutdown();
//
//        try {
//            f1.get(); // 첫 번째 작업이 완료될 때까지 대기
//            f2.get(); // 두 번째 작업이 완료될 때까지 대기
//        } catch (Exception  e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}