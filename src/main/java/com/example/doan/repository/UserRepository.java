package com.example.doan.repository;

import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByRole(USER_ROLE role);
//    List<User> findByAccountStatus(AccountStatus status);

}
