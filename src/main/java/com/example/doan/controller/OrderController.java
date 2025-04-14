package com.example.doan.controller;

import com.example.doan.doman.PaymentMethod;
import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.*;
import com.example.doan.response.PaymentResponse;
import com.example.doan.service.CartService;
import com.example.doan.service.OrderService;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    @PostMapping()
    public ResponseEntity<PaymentResponse> createOrderHandler(
            @RequestBody Address shippingAddress,
            @RequestParam PaymentMethod paymentMethod,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        Cart cart = cartService.findUserCart(user);

        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart);
        // 4. Xử lý theo phương thức thanh toán
        PaymentResponse res = new PaymentResponse();

//        if (paymentMethod.equals(PaymentMethod.COD)) {
//            // Thanh toán khi nhận hàng
//            res = new PaymentResponse("Đặt hàng thành công. Thanh toán khi nhận hàng.", null);
//
//        } else if (paymentMethod.equals(PaymentMethod.VNPAY)) {
//            // Thanh toán qua VNPAY
//            PaymentOrder paymentOrder = paymentService.createVNPayOrder(user, orders);
//            response = new PaymentResponse("Vui lòng thanh toán qua VNPAY", paymentOrder.getPaymentUrl());
//
//        } else {
//            throw new IllegalArgumentException("Phương thức thanh toán không hỗ trợ");
//        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @GetMapping("/user")
    public ResponseEntity<List<Order>> userOrdersHistoryHandler(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders= orderService.userOrderHistory(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }
    @GetMapping("/item/{orderItemID}")
    public ResponseEntity<OrderItem> getOrderItemById(
            @PathVariable Long orderItemID,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        OrderItem orderItem=orderService.getOrderItemById(orderItemID);

        return new ResponseEntity<>(orderItem, HttpStatus.ACCEPTED);
    }
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        if (user.getRole() != USER_ROLE.ROLE_ADMIN ) {
            throw new Exception("Bạn không có quyền hủy đơn hàng.");
        }

        Order order = orderService.cancelOrder(orderId, user);

//        Report report = reportService.getReport(seller);
//        report.setCancelledOrders(report.getCancelledOrders() + 1);
//        report.setTotalRefunds(report.getTotalRefunds() + order.getTotalDiscountPrice());
//        reportService.updateReport(report);

        return ResponseEntity.ok(order);
    }


}
