package com.example.doan.service;

import com.example.doan.modal.Order;
import com.example.doan.modal.PaymentOrder;
import com.example.doan.modal.User;

import java.util.Set;

public interface PaymentService {
    PaymentOrder createOrder(User user, Set<Order> orders);
    PaymentOrder getPaymentOrderById(Long orderId) throws Exception;
    PaymentOrder getPaymentOrderByPaymentId(String orderId) throws Exception;
    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, String paymentLinkId);
    PaymentOrder createCODPaymentOrder(User user, Set<Order> orders);
}
