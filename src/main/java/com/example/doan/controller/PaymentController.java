package com.example.doan.controller;

import com.example.doan.config.VNPayConfig;
import com.example.doan.doman.OrderStatus;
import com.example.doan.doman.PaymentStatus;
import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.Order;
import com.example.doan.modal.PaymentOrder;
import com.example.doan.modal.Report;
import com.example.doan.modal.User;
import com.example.doan.repository.OrderRepository;
import com.example.doan.response.ApiResponse;
import com.example.doan.response.PaymentResponse;
import com.example.doan.response.TransactionStatusResponse;
import com.example.doan.service.PaymentService;
import com.example.doan.service.ReportService;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;
    private final ReportService reportService;
    private final OrderRepository orderRepository;

    @GetMapping("/check-payment")
    public ResponseEntity<TransactionStatusResponse> checkPayment(
            @RequestParam Map<String, String> allParams
    ) {
        String secureHash = allParams.remove("vnp_SecureHash");
        String hashData = VNPayConfig.hashAllFields(allParams); // tạo chuỗi hash
        String myHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData);

        // 1. Kiểm tra chữ ký hợp lệ
        if (!myHash.equals(secureHash)) {
            return ResponseEntity.badRequest().body(TransactionStatusResponse.builder()
                    .status("Failed")
                    .message("Chữ ký không hợp lệ")
                    .data("https://your-client.com/payment-fail") // link tới trang thất bại
                    .build());
        }

        String responseCode = allParams.get("vnp_ResponseCode");
        String orderIdStr = allParams.get("vnp_OrderInfo"); // bạn truyền orderId lúc tạo order
        Long orderId = Long.parseLong(orderIdStr);

        // 2. Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body(TransactionStatusResponse.builder()
                    .status("Failed")
                    .message("Không tìm thấy đơn hàng")
                    .data("https://your-client.com/payment-fail")
                    .build());
        }

        // 3. Giao dịch thành công
        if ("00".equals(responseCode)) {
            order.setOrderStatus(OrderStatus.SUCCESS);
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            orderRepository.save(order);

            return ResponseEntity.ok(TransactionStatusResponse.builder()
                    .status("Success")
                    .message("Thanh toán thành công")
                    .data("https://your-client.com/payment-success") // link tới trang thành công
                    .build());
        }

        // 4. Giao dịch thất bại
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);

        return ResponseEntity.ok(TransactionStatusResponse.builder()
                .status("Failed")
                .message("Thanh toán thất bại")
                .data("https://your-client.com/payment-fail")
                .build());
    }

}
//    @GetMapping("/{paymentId}")
//    public ResponseEntity<ApiResponse> paymentSuccessHandler(
//            @PathVariable String paymentId,
//            @RequestParam String paymentLinkId,
//            @RequestHeader("Authorization") String jwt) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
//
//        PaymentResponse paymentResponse;
//
//        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentId);
//
//        boolean paymentSuccess =  paymentService.proceedPaymentOrder(paymentOrder,paymentId,paymentLinkId);
//        if (paymentSuccess) {
//
//            int totalCustomers = userService.getAllCustomers(USER_ROLE.ROLE_CUSTOMER).size();
//
//            for (Order order : paymentOrder.getOrders()) {
//                User user1 = userService.getUserById(order.getId());
//                Report report = reportService.getReport(user1);
//                report.setTotalOrders(report.getTotalOrders()+1);
//                report.setTotalEarnings(report.getTotalEarnings()+order.getTotalDiscountPrice());
//                report.setTotalSales(report.getTotalSales()+order.getOderItems().size());
//                report.setTotalCustomers(totalCustomers);
//                reportService.updateReport(report);
//            }
//        }
//        ApiResponse res = new ApiResponse();
//        res.setMessage("Thanh toán thành công");
//        return new ResponseEntity<>(res, HttpStatus.CREATED);
//    }

