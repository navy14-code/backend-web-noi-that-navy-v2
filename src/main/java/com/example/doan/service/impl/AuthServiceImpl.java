package com.example.doan.service.impl;

import com.example.doan.config.JwtProvider;
import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.User;
import com.example.doan.modal.VerificationCode;
import com.example.doan.repository.VerificationCodeRepository;
import com.example.doan.repository.UserRepository;
import com.example.doan.request.LoginRequest;
import com.example.doan.request.SignupRequest;
import com.example.doan.response.AuthResponse;
import com.example.doan.service.AuthService;
import com.example.doan.service.EmailService;
import com.example.doan.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;
    private final customerUserServiceImpl customerUserService;

    @Override
    public void sentLoginOtp(String email,String password, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            // Giả sử bạn chỉ còn User role là ADMIN và CUSTOMER
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new Exception("User không tồn tại với email đã cung cấp");
            }
        }
        // Xử lý OTP
        VerificationCode existing = verificationCodeRepository.findByEmail(email);
        if (existing != null) {
            verificationCodeRepository.delete(existing);
        }

        String otp = OtpUtils.generateOtp();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "Mã OTP đăng nhập/đăng ký";
        String text = "Mã OTP của bạn là: " + otp;

        emailService.sendVerificationEmail(email, otp, subject, text);
    }

    @Override
    public String createUser(SignupRequest req) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("wrong otp!");
        }
        User user = userRepository.findByEmail(req.getEmail());

        if (user == null) {
            User createUser = new User();
            createUser.setEmail(req.getEmail());
            createUser.setFullName(req.getFullName());
            createUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createUser.setPhone("0888291733");
            createUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user= userRepository.save(createUser);

//            Cart cart = new Cart();
//            cart.setUser(user);
//            cartReponstitory.save(cart);
        }
        List<GrantedAuthority> authorities= new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);


        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signing(LoginRequest req) throws Exception {
        String username= req.getEmail();
        String otp= req.getOtp();

        Authentication authentication = authenticate(username, otp);
        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Đăng nhập thành công ");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName= authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) throws Exception {
        UserDetails userDetails = customerUserService.loadUserByUsername(username);


        if(userDetails==null){
            throw new BadCredentialsException("invalid username");
        }
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if(verificationCode==null || !verificationCode.getOtp().equals(otp)){
            throw new Exception("Mã OTP không chính xác");
        }

        verificationCodeRepository.delete(verificationCode);


        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
