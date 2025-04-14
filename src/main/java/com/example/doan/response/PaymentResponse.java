package com.example.doan.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String message;
    private String paymentUrl;
}
