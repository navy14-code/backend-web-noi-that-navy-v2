package com.example.doan.service;

import com.example.doan.doman.USER_ROLE;
import com.example.doan.request.LoginRequest;
import com.example.doan.response.AuthResponse;
import com.example.doan.request.SignupRequest;

public interface AuthService {
    void sentLoginOtp(String  email,String password, USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req) throws Exception;
}
