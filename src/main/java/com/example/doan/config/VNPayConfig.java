package com.example.doan.config;

public class VNPayConfig {
    public static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String TMN_CODE = "Your_TmnCode";
    public static final String HASH_SECRET = "Your_HashSecret";
    public static final String RETURN_URL = "http://localhost:8080/api/orders/vnpay-return"; // URL callback sau khi thanh toán
}
