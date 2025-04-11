package com.example.doan.controller;

import com.example.doan.doman.USER_ROLE;
import com.example.doan.repository.UserRepository;
import com.example.doan.request.*;
import com.example.doan.response.ApiResponse;
import com.example.doan.response.AuthResponse;
import com.example.doan.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandle(@RequestBody SignupRequest req) throws Exception {

        String jwt= authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("Đăng ký thành công ");
        res.setRole(USER_ROLE.ROLE_CUSTOMER);

        return  ResponseEntity.ok(res);
    }

    @PostMapping("/sent/login-otp")
    public ResponseEntity<ApiResponse> sendOtpHandle(@RequestBody LoginOtpRequest req) throws Exception {

            authService.sentLoginOtp(req.getEmail(), req.getPassword(), req.getRole());

            ApiResponse res = new ApiResponse();

            res.setMessage("Gửi mã OTP thành công");

            return  ResponseEntity.ok(res);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandle(@RequestBody LoginRequest req) throws Exception {

        AuthResponse authResponse = authService.signing(req);

        return  ResponseEntity.ok(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgerPasswordRequest req) throws Exception {

            authService.forgotPassword(req.getEmail());

            ApiResponse res = new ApiResponse();

            res.setMessage("Gửi mã OTP thành công");

            return  ResponseEntity.ok(res);

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) throws Exception {

            authService.resetPasswordOtp(req.getEmail(), req.getOtp(), req.getNewPassword());

            ApiResponse res = new ApiResponse();

            res.setMessage("Đặt lại mật khẩu thành công ");

            return  ResponseEntity.ok(res);
    }

}
