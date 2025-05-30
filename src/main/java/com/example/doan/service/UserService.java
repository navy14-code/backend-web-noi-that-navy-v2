package com.example.doan.service;

import com.example.doan.doman.USER_ROLE;
import com.example.doan.exceptions.UserException;
import com.example.doan.modal.User;
import com.example.doan.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User findUserByJwtToken(String jwt) throws Exception;
    User getUserProfile(String jwt) throws Exception;
    User getUserByEmail(String email) throws Exception;
    User createUser(User user) throws Exception;
    User getUserById(Long id) throws UserException;
    User updateUser(Long id, UserRequest req) throws Exception;
    void deleteUser(Long id) throws Exception;
    List<User> getAllUsers();
    List<User> getAllCustomers(USER_ROLE role );
    Page<User> findAllByRoleCustomer(USER_ROLE role, int page, int size);


}
