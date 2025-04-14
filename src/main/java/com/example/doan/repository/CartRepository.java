package com.example.doan.repository;

import com.example.doan.modal.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository  extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long id);
}
