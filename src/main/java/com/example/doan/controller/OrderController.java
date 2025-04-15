package com.example.doan.controller;

import com.example.doan.doman.OrderStatus;
import com.example.doan.doman.PaymentMethod;
import com.example.doan.doman.PaymentStatus;
import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.*;
import com.example.doan.response.PaymentResponse;
import com.example.doan.service.CartService;
import com.example.doan.service.OrderService;
import com.example.doan.service.UserService;
import com.example.doan.service.VNPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart, paymentMethod);

        PaymentResponse res;
        if (paymentMethod == PaymentMethod.COD) {
            // Đặt trạng thái đơn hàng là "Chờ xử lý"
            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.PENDING);
                order.setPaymentStatus(PaymentStatus.PENDING); // COD là chưa thanh toán
                orderService.saveOrder(order); // Lưu đơn hàng vào cơ sở dữ liệu
            }
            // Trả về thông báo thành công cho thanh toán COD
            res = new PaymentResponse("Đặt hàng thành công. Thanh toán khi nhận hàng.", null); // Truyền giá trị đúng cho URL
        }
//        else if (paymentMethod == PaymentMethod.VNPAY) {
//            // Tạo URL thanh toán VNPAY
//            for (Order order : orders) {
//                order.setOrderStatus(OrderStatus.PENDING); // Đặt trạng thái đơn hàng là "Chờ xử lý"
//                order.setPaymentStatus(PaymentStatus.PENDING); // Thanh toán chờ xử lý
//
//                // Tạo URL thanh toán từ dịch vụ VNPAY
//                String paymentUrl = vnPayService.createPaymentUrl(order); // Giả sử bạn có service `vnPayService` để tạo URL thanh toán
//
//                order.setPaymentUrl(paymentUrl); // Lưu URL thanh toán vào đơn hàng
//                orderService.saveOrder(order); // Lưu đơn hàng vào cơ sở dữ liệu
//            }
//            // Trả về URL thanh toán VNPAY
//            res = new PaymentResponse("Vui lòng thanh toán qua VNPAY.", paymentUrl); // Trả về URL thanh toán VNPAY
//        }
        // Nếu phương thức thanh toán không hợp lệ, trả về lỗi
        else {
            res = new PaymentResponse("Phương thức thanh toán không hợp lệ", null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST); // Trả về mã lỗi 400
        }

        return new ResponseEntity<>(res, HttpStatus.OK);

    }
//    @PostMapping("/vnpay-return")
//    public ResponseEntity<String> handleVNPayCallback(@RequestParam Map<String, String> params) throws Exception {
//        String vnp_ResponseCode = params.get("vnp_ResponseCode");
//        Long orderId = Long.valueOf(params.get("vnp_TxnRef")); // Lấy ID đơn hàng từ callback
//
//        Order order = orderService.findOrderById(orderId);
//        if ("00".equals(vnp_ResponseCode)) { // "00" là mã phản hồi thành công
//            order.setPaymentStatus(PaymentStatus.PROCESSING);
//            order.setOrderStatus(OrderStatus.CONFIRMED);
//        } else {
//            order.setPaymentStatus(PaymentStatus.FAILED);
//        }
//        orderService.saveOrder(order); // Lưu thay đổi vào DB
//        return ResponseEntity.ok("Payment status updated.");
//    }


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
