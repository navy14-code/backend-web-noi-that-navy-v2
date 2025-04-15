package com.example.doan.controller;

import com.example.doan.doman.OrderStatus;
import com.example.doan.modal.Order;
import com.example.doan.modal.User;
import com.example.doan.service.OrderService;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersHandle(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.getUserProfile(jwt);
        List<Order> orders = orderService.getAllOrders(user.getId());

        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{orderId}/status/{orderStats}")
    public ResponseEntity<Order> updateOrderHandle(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId,
            @PathVariable OrderStatus orderStatus
    ) throws Exception {

        Order order = orderService.updateOrderStatus(orderId, orderStatus);

        return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
    }
}
