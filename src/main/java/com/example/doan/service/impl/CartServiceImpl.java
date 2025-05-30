package com.example.doan.service.impl;

import com.example.doan.modal.Cart;
import com.example.doan.modal.CartItem;
import com.example.doan.modal.Product;
import com.example.doan.modal.User;
import com.example.doan.repository.CartItemRepository;
import com.example.doan.repository.CartRepository;
import com.example.doan.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    public CartItem addCartItem(User user, Product product, String size, int quality) {

        Cart cart= findUserCart(user);

        CartItem isPresent = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

        if (isPresent == null) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quality);
            cartItem.setUserId(user.getId());
            cartItem.setSize(size);

            int totalPrice= quality*product.getPrice();
            cartItem.setPrice(totalPrice);
            cartItem.setDiscountPrice(quality*product.getDiscountPrice());

            cart.getCartItems().add(cartItem);
            cartItem.setCart(cart);

            return cartItemRepository.save(cartItem);
        }
        return isPresent;
    }

    @Override
    public Cart findUserCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new HashSet<>()); // tránh null
            cart.setTotalItem(0);
            cart.setPrice(0);
            cart.setDiscountPrice(0);
            cart.setDiscount(0);
            cart = cartRepository.save(cart);
        }

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;
        for (CartItem cartItem: cart.getCartItems()){
            totalPrice+=cartItem.getPrice();
            totalDiscountedPrice+=cartItem.getDiscountPrice();
            totalItem+=cartItem.getQuantity();
        }

        cart.setPrice(totalPrice);
        cart.setTotalItem(totalItem);
        cart.setDiscountPrice(totalDiscountedPrice);
        cart.setDiscount((calculateDiscountPercent(totalPrice, totalDiscountedPrice)));
        cart.setTotalItem(totalItem);

        return cart;
    }
    private int calculateDiscountPercent(int price, int discountPrice  ) {
        if(price <= 0) {
            return 0;
        }
        double discount = price - discountPrice;
        double discountPercent =(discount/price)*100;
        return (int)discountPercent;

    }
}
