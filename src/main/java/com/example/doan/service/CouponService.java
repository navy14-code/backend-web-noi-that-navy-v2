package com.example.doan.service;

import com.example.doan.modal.Cart;
import com.example.doan.modal.Coupon;
import com.example.doan.modal.User;

import java.util.List;

public interface CouponService {
    Cart applyCoupon(String code, double orderValue, User user) throws Exception;
    Cart removeCoupon(String code, User user) throws Exception;
    Coupon findCouponById(Long id) throws Exception;
    Coupon createCoupon(Coupon coupon);
    List<Coupon> getAllCoupons();
    void deleteCoupon(Long id) throws Exception;
}
