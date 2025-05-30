package com.example.doan.controller;

import com.example.doan.config.VNPayConfig;
import com.example.doan.exceptions.DataNotFoundException;
import com.example.doan.repository.OrderRepository;
import com.example.doan.response.PaymentResponse1;
import com.example.doan.response.TransactionStatusResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController2 {
    private final OrderRepository orderRepository;

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            HttpServletRequest request,
            @RequestParam(value = "final_price") BigDecimal amount,
            @RequestParam(value = "order_id") Long orderId
    ) throws UnsupportedEncodingException {
        String orderType = "other";
//        Integer orderId = 1;
//        amount = amount*2400000;
        amount = amount.multiply(BigDecimal.valueOf(100));
//            String bankCode = req.getParameter("bankCode");;

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
//            String vnp_IpAddr = VNPayConfig1.getIpAddress(req);

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderId.toString());
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

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;


        PaymentResponse1 paymentResponse = PaymentResponse1.builder()
                .status("OK")
                .message("")
                .url(paymentUrl)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponse);
    }

    @GetMapping("/check-payment")
    public ResponseEntity<?> transaction(
            @RequestParam(value = "vnp_Amount") BigDecimal amount,
            @RequestParam(value = "vnp_BankCode") String bankCode,
            @RequestParam(value = "vnp_CardType", required = false) String cardType,
            @RequestParam(value = "vnp_OrderInfo") String orderInfo,
            @RequestParam(value = "vnp_PayDate") String payDate,
            @RequestParam(value = "vnp_ResponseCode") String responseCode,
            @RequestParam(value = "vnp_ResponseCode") String tmnCode,
            @RequestParam(value = "vnp_TransactionNo") String transactionNo,
            @RequestParam(value = "vnp_TransactionStatus") String transactionStatus,
            @RequestParam(value = "vnp_TxnRef") String txnRef,
            @RequestParam(value = "vnp_SecureHash") String secureHash
    ) throws DataNotFoundException {
        TransactionStatusResponse transactionStatusResponse ;
        // payment successfully
        if (responseCode.equals("00")) {
            transactionStatusResponse = TransactionStatusResponse.builder()
                    .status("Accepted")
                    .message("")
                    .data("https://sandbox.vnpayment.vn/apis/vnpay-demo/")
                    .build();

            return ResponseEntity.ok(transactionStatusResponse);
        } else {
            Long orderId = Long.getLong(orderInfo);
//            Order order = this.orderRepository.findById(orderId)
//                    .orElseThrow(() -> new DataNotFoundException(
//                            "" + orderId
//                    ));
//            order.setPaymentMethod("other");
//            order.setStatus(OrderStatusEnum.CANCELLED);
//            this.orderRepository.save(order);
            return ResponseEntity.badRequest().body(TransactionStatusResponse.builder()
                    .status("Failed")
                    .message("")
                    .data("https://www.facebook.com/")  // Redirect to the homeclient page
                    .build());
        }
    }
}