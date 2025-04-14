package com.example.doan.service.impl;

import com.example.doan.modal.CartItem;
import com.example.doan.modal.User;
import com.example.doan.repository.CartItemRepository;
import com.example.doan.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws Exception {
        CartItem item = findCartItemById(id);

        User cartItemUser= item.getCart().getUser();

        if(cartItemUser.getId().equals(userId)){
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(item.getQuantity()*item.getProduct().getPrice());
            item.setDiscountPrice(item.getQuantity()*item.getProduct().getDiscountPrice());
            return cartItemRepository.save(item);
        }
        throw new Exception("Bạn không thể cập nhật mặt hàng trong giỏ hàng ");
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws Exception {
        CartItem item = findCartItemById(cartItemId);

        User cartItemUser= item.getCart().getUser();

        if(cartItemUser.getId().equals(userId)){
            cartItemRepository.delete(item);
        }
        else throw new Exception("Bạn không thể xóa mặt hàng này");

    }

    @Override
    public CartItem findCartItemById(Long id) throws Exception {
        return cartItemRepository.findById(id).orElseThrow(()->
                new Exception("Không tìm thấy mặt hàng với id " + id));
    }
}
