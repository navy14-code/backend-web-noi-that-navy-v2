package com.example.doan.controller;

import com.example.doan.config.VNPayConfig;
import com.example.doan.doman.OrderStatus;
import com.example.doan.doman.PaymentMethod;
import com.example.doan.doman.PaymentStatus;
import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.*;
import com.example.doan.response.PaymentResponse;
import com.example.doan.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity<PaymentResponse> createOrderHandler(
            @RequestBody Address shippingAddress,
            @RequestParam PaymentMethod paymentMethod,
            @RequestHeader("Authorization") String jwt,
            HttpServletRequest request) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);

        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart, paymentMethod);

        PaymentResponse res;
        if (paymentMethod == PaymentMethod.COD) {
            // Đặt trạng thái đơn hàng là "Chờ xử lý"
            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.SUCCESS);
                order.setPaymentStatus(PaymentStatus.NOT_PAID);
                orderService.saveOrder(order); // Lưu đơn hàng vào cơ sở dữ liệu
            }
            // Trả về thông báo thành công cho thanh toán COD
            res = new PaymentResponse("Đặt hàng thành công. Thanh toán khi nhận hàng.", null); // Truyền giá trị đúng cho URL
        }
        else if (paymentMethod == PaymentMethod.VNPAY) {
            // Đặt trạng thái đơn hàng là "Chờ thanh toán"
            for (Order order : orders) {
                order.setOrderStatus(OrderStatus.SUCCESS);
                order.setPaymentStatus(PaymentStatus.NOT_PAID);
                orderService.saveOrder(order);
            }
            // --- Tạo URL thanh toán từ VNPayConfig ---
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Order order : orders) {
                totalAmount = totalAmount.add(BigDecimal.valueOf(order.getTotalPrice()));
            }
            BigDecimal amount = totalAmount.multiply(BigDecimal.valueOf(100));
            String orderType = "other";
            String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount.longValue()));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_BankCode", "NCB");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang");
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", VNPayConfig.getIpAddress(request));

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (int i = 0; i < fieldNames.size(); i++) {
                String key = fieldNames.get(i);
                String value = vnp_Params.get(key);

                if (value != null && !value.isEmpty()) {
                    hashData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII.toString()))
                            .append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));

                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query;

            res = new PaymentResponse("Chuyển hướng đến trang thanh toán VNPAY", paymentUrl);
            return new ResponseEntity<>(res, HttpStatus.OK); // Trả về ngay sau khi tạo UR
        }
        else {
            res = new PaymentResponse("Phương thức thanh toán không hợp lệ", null);
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST); // Trả về mã lỗi 400
        }

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

        Report report = reportService.getReport(user);

        report.setCanceledOrders(report.getCanceledOrders()+1);
        report.setTotalRefunds(report.getTotalRefunds()+ order.getTotalDiscountPrice());
        reportService.updateReport(report);

        return ResponseEntity.ok(order);
    }

}
