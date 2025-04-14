package com.example.doan.repository;

import com.example.doan.modal.Cart;
import com.example.doan.modal.CartItem;
import com.example.doan.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndProductAndSize(Cart cart, Product product, String size);

}
