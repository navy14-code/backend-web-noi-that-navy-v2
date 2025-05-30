package com.example.doan.service;

import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.User;
import com.example.doan.request.auth.LoginRequest;
import com.example.doan.response.AuthResponse;
import com.example.doan.request.auth.SignupRequest;

public interface AuthService {
    void sentLoginOtp(String  email,String password, USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse login(LoginRequest req) throws Exception;
    void forgotPassword(String email) throws Exception;
    void resetPasswordOtp(String email, String otp, String newPassword) throws Exception;
    void sentSignOtp(String  email, USER_ROLE role) throws Exception;

}
