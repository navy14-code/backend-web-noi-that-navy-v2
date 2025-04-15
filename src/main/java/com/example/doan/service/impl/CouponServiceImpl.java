package com.example.doan.service.impl;

import com.example.doan.modal.Cart;
import com.example.doan.modal.Coupon;
import com.example.doan.modal.User;
import com.example.doan.repository.CartRepository;
import com.example.doan.repository.CategoryRepository;
import com.example.doan.repository.CouponRepository;
import com.example.doan.repository.UserRepository;
import com.example.doan.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    public Cart applyCoupon(String code, double orderValue, User user) throws Exception {
        Coupon coupon = couponRepository.findByCode(code);

        Cart cart = cartRepository.findByUserId(user.getId());

        if (coupon == null) {
            throw new Exception("Mã giảm giá không tồn tại");
        }
        if(user.getUsedCoupons().contains(coupon)){
            throw new Exception("Mã giảm giá đã được sử dụng");
        }
        if(orderValue < coupon.getMinimumOderValue()){
            throw new Exception("Giá trị đơn hàng tối thiểu" +coupon.getMinimumOderValue());
        }
        if (coupon.isActive()&& LocalDate.now().isAfter(coupon.getValidityStartDate())
         && LocalDate.now().isBefore(coupon.getValidityEndDate())){

            user.getUsedCoupons().add(coupon);
            userRepository.save(user);

            double discountedPrice = (cart.getDiscountPrice()*coupon.getDiscountPercentage())/100;

            cart.setDiscountPrice(cart.getDiscountPrice()-discountedPrice);
            cart.setCouponCode(code);
            cartRepository.save(cart);

        }
        throw new Exception("Phiếu giảm giá không hợp lệ");
    }

    @Override
    public Cart removeCoupon(String code, User user) throws  Exception {
        Coupon coupon = couponRepository.findByCode(code);

        if (coupon == null) {
            throw new Exception("Mã giảm giá không tồn tại");
        }
        Cart cart = cartRepository.findByUserId(user.getId());

        double discountedPrice = (cart.getDiscountPrice()*coupon.getDiscountPercentage())/100;

        cart.setDiscountPrice(cart.getDiscountPrice()+discountedPrice);
        cart.setCouponCode(null);

        return cartRepository.save(cart);
    }

    @Override
    public Coupon findCouponById(Long id) throws Exception {
        return couponRepository.findById(id).orElseThrow(()-> new Exception("Không tim thấy mã giảm gi"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCoupon(Long id) throws Exception {
        findCouponById(id);
        couponRepository.deleteById(id);
    }
}
