package com.example.doan.request;

import com.example.doan.doman.USER_ROLE;
import lombok.Data;

@Data
public class LoginOtpRequest {
    private String email;
    private String password;
    private String otp;
    private USER_ROLE role;
}
