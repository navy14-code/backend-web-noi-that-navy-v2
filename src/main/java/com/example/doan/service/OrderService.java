package com.example.doan.service;

import com.example.doan.doman.OrderStatus;
import com.example.doan.doman.PaymentMethod;
import com.example.doan.modal.*;

import java.util.List;
import java.util.Set;

public interface OrderService {
    Set<Order> createOrder(User user, Address shippingAddress, Cart cart, PaymentMethod paymentMethod);
    Order findOrderById(long id) throws Exception;
    List<Order> userOrderHistory(long userId);
    List<Order> getAllOrders(long userId);
    Order updateOrderStatus(long orderId, OrderStatus orderStatus) throws Exception;
    Order cancelOrder(long orderId, User user) throws Exception;
    OrderItem getOrderItemById(long Id) throws Exception;
    Order saveOrder(Order order);
}
