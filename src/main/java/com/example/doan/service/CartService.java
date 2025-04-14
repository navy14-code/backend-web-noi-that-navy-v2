package com.example.doan.service;

import com.example.doan.modal.Cart;
import com.example.doan.modal.CartItem;
import com.example.doan.modal.Product;
import com.example.doan.modal.User;

public interface CartService {
    public CartItem addCartItem(
            User user,
            Product product,
            String size,
            int quality
    );
    public Cart findUserCart(User user);
}
