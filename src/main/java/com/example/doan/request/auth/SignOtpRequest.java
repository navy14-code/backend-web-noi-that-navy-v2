package com.example.doan.request.auth;

import com.example.doan.doman.USER_ROLE;
import lombok.*;

@Data
public class SignOtpRequest {
    private String email;
    private String otp;
    private USER_ROLE role;
}
