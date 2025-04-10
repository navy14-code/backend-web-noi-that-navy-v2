package com.example.doan.service.impl;

import com.example.doan.config.JwtProvider;
import com.example.doan.modal.User;
import com.example.doan.repository.UserRepository;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email= jwtProvider.getEmailFromJwtToken(jwt);


        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user= userRepository.findByEmail(email);
        if(user==null){
            throw new Exception("Không tìm thấy người dùng với email: "+ email);
        }
        return user;
    }
}
