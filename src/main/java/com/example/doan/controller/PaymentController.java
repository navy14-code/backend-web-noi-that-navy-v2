package com.example.doan.controller;

import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.Order;
import com.example.doan.modal.PaymentOrder;
import com.example.doan.modal.Report;
import com.example.doan.modal.User;
import com.example.doan.response.ApiResponse;
import com.example.doan.response.PaymentResponse;
import com.example.doan.service.PaymentService;
import com.example.doan.service.ReportService;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;
    private final ReportService reportService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse> paymentSuccessHandler(
            @PathVariable String paymentId,
            @RequestParam String paymentLinkId,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);

        PaymentResponse paymentResponse;

        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentId);

        boolean paymentSuccess =  paymentService.proceedPaymentOrder(paymentOrder,paymentId,paymentLinkId);
        if (paymentSuccess) {

            int totalCustomers = userService.getAllCustomers(USER_ROLE.ROLE_CUSTOMER).size();

            for (Order order : paymentOrder.getOrders()) {
                User user1 = userService.getUserById(order.getId());
                Report report = reportService.getReport(user1);
                report.setTotalOrders(report.getTotalOrders()+1);
                report.setTotalEarnings(report.getTotalEarnings()+order.getTotalDiscountPrice());
                report.setTotalSales(report.getTotalSales()+order.getOderItems().size());
                report.setTotalCustomers(totalCustomers);
                reportService.updateReport(report);
            }
        }
        ApiResponse res = new ApiResponse();
        res.setMessage("Thanh toán thành công");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}

