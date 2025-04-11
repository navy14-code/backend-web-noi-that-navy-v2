package com.example.doan.service;

import com.example.doan.exceptions.UserException;
import com.example.doan.modal.User;
import com.example.doan.request.UserRequest;

import java.util.List;

public interface UserService {
    User getUserProfile(String jwt) throws Exception;
    User getUserByEmail(String email) throws Exception;
//    User createUser(User user) throws Exception;
    User getUserById(Long id) throws UserException;
//    List<User> getAllUsers(AccountStatus status);
    User updateUser(Long id, UserRequest req) throws Exception;
    void deleteUser(Long id) throws Exception;
    List<User> getAllUsers();
}
