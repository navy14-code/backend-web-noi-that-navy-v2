package com.example.doan.service;

import com.example.doan.config.VNPayConfig;
import com.example.doan.modal.Order;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class VNPayService {
    public String createPaymentUrl(Order order) throws Exception {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "billpayment";
        String vnp_TxnRef = String.valueOf(order.getId()); // ID đơn hàng
        String vnp_IpAddr = "127.0.0.1"; // IP Address của user
        String vnp_TmnCode = VNPayConfig.TMN_CODE;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(order.getTotalPrice() * 100)); // Giá trị tính bằng VND * 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng: " + order.getOrderId());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.RETURN_URL);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Tạo chữ ký
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = vnp_Params.get(fieldName);
            if ((value != null) && (value.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, "UTF-8")).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, "UTF-8")).append('&');
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(VNPayConfig.HASH_SECRET, hashData.toString());
        queryUrl += "vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.VNPAY_URL + "?" + queryUrl;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        javax.crypto.Mac hmacSHA512 = javax.crypto.Mac.getInstance("HmacSHA512");
        javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512");
        hmacSHA512.init(secretKey);
        byte[] hash = hmacSHA512.doFinal(data.getBytes("UTF-8"));
        return HexFormat.of().formatHex(hash).toUpperCase();
    }
//public static String hmacSHA512(final String key, final String data) {
//    try {
//
//        if (key == null || data == null) {
//            throw new NullPointerException();
//        }
//        final Mac hmac512 = Mac.getInstance("HmacSHA512");
//        byte[] hmacKeyBytes = key.getBytes();
//        final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
//        hmac512.init(secretKey);
//        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
//        byte[] result = hmac512.doFinal(dataBytes);
//        StringBuilder sb = new StringBuilder(2 * result.length);
//        for (byte b : result) {
//            sb.append(String.format("%02x", b & 0xff));
//        }
//        return sb.toString();
//
//    } catch (Exception ex) {
//        return "";
//    }
//}
}
