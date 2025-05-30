package com.example.doan.service.impl;

import com.example.doan.doman.PaymentMethod;
import com.example.doan.doman.PaymentOrderStatus;
import com.example.doan.doman.PaymentStatus;
import com.example.doan.modal.Order;
import com.example.doan.modal.PaymentOrder;
import com.example.doan.modal.User;
import com.example.doan.repository.OrderRepository;
import com.example.doan.repository.PaymentOrderRepository;
import com.example.doan.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentOrder createOrder(User user, Set<Order> orders) {
        Long amount =orders.stream().mapToLong(Order::getTotalDiscountPrice).sum();
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setAmount(amount);
        paymentOrder.setUser(user);
        paymentOrder.setOrders(orders);
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long orderId) throws Exception {
        return paymentOrderRepository.findById(orderId).orElseThrow(() ->
                new Exception("Không tìm thấy đơn hàng thanh toán"));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String orderId) throws Exception {
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(orderId);

        if (paymentOrder == null) {
            throw new Exception("Không tìm thấy đơn đặt hàng với link id liên kết thanh toán ");
        }
        return paymentOrder;
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) {
        // Kiểm tra trạng thái PaymentOrder
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            if (paymentOrder.getPaymentMethod() == PaymentMethod.COD) {
                // Xử lý thanh toán COD: Đơn hàng được đặt thành công nhưng chưa thanh toán
                Set<Order> orders = paymentOrder.getOrders();
                for (Order order : orders) {
                    order.setPaymentStatus(PaymentStatus.NOT_PAID); // Đơn hàng chưa thanh toán
                    orderRepository.save(order);
                }
                paymentOrder.setStatus(PaymentOrderStatus.PENDING); // Đơn hàng chờ thanh toán (COD)
            }
            else if (paymentOrder.getPaymentMethod() == PaymentMethod.VNPAY) {
                // Xử lý thanh toán VNPay: Đơn hàng chờ thanh toán qua cổng VNPay
                paymentOrder.setStatus(PaymentOrderStatus.PENDING); // Giữ trạng thái chờ thanh toán
            }

            // Lưu trạng thái PaymentOrder
            paymentOrderRepository.save(paymentOrder);
            return true;

        }
        else {
            // Nếu trạng thái không phải PENDING, không thực hiện gì
            return false;
        }
    }

    @Override
    public PaymentOrder createCODPaymentOrder(User user, Set<Order> orders) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);  // Gán thông tin người dùng
        paymentOrder.setOrders(orders); // Gán danh sách đơn hàng vào PaymentOrder
        paymentOrder.setAmount(orders.stream().mapToLong(Order::getTotalDiscountPrice).sum()); // Tổng giá trị đơn hàng
        paymentOrder.setPaymentMethod(PaymentMethod.COD); // Đặt phương thức thanh toán là COD
        paymentOrder.setStatus(PaymentOrderStatus.PENDING); // Đặt trạng thái PaymentOrder là PENDING (Chờ thanh toán)

        return paymentOrderRepository.save(paymentOrder);
    }
}
