package com.example.doan.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PaymentResponse implements Serializable {
    private String message;
    private String paymentUrl;
}
