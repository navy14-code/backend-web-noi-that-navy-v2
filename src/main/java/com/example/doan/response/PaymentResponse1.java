package com.example.doan.response;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@Component
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse1 {
    private String status;
    private String message;
    private String url;
}
