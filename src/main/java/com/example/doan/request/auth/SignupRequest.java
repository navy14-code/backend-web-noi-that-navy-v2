package com.example.doan.request.auth;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String fullName;
    private String password;
    private String phone;
}
