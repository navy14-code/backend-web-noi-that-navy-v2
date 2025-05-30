package com.example.doan.service.impl;

import com.example.doan.doman.OrderStatus;
import com.example.doan.doman.PaymentMethod;
import com.example.doan.doman.PaymentStatus;
import com.example.doan.modal.*;
import com.example.doan.repository.AddressRepository;
import com.example.doan.repository.CartRepository;
import com.example.doan.repository.OrderItemRepository;
import com.example.doan.repository.OrderRepository;
import com.example.doan.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    @Override
    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart, PaymentMethod paymentMethod) {
        // Nếu địa chỉ chưa được lưu thì thêm vào
        if(!user.getAddresses().contains(shippingAddress)) {
            user.getAddresses().add(shippingAddress);
        }
        Address address = addressRepository.save(shippingAddress);
        // Nhóm các sản phẩm theo người bán (userId)
        Map<Long, List<CartItem>> itemsByUser= cart.getCartItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct()
                        .getUser().getId()));
        Set<Order> orders = new HashSet<>();
        // Lặp qua các nhóm sản phẩm theo người bán
        for(Map.Entry<Long, List<CartItem>> entry : itemsByUser.entrySet()) {
            Long userId = entry.getKey();
            List<CartItem> items = entry.getValue();
            // Tính tổng giá trị đơn hàng và số lượng sản phẩm
            int totalOrderPrice=items.stream().mapToInt(CartItem::getDiscountPrice).sum();
            int totalItem= items.stream().mapToInt(CartItem::getQuantity).sum();

            Order createdOrder = new Order();
            createdOrder.setUser(user);
            createdOrder.setTotalPrice(totalOrderPrice);
            createdOrder.setTotalDiscountPrice(totalOrderPrice);
            createdOrder.setTotalItem(totalItem);
            createdOrder.setShippingAddress(address);
            createdOrder.setOrderStatus(OrderStatus.PENDING);

            // Xử lý phương thức thanh toán COD
            if (paymentMethod == PaymentMethod.COD) {
                createdOrder.setPaymentStatus(PaymentStatus.NOT_PAID); // COD là chưa thanh toán
            }
            else if (paymentMethod == PaymentMethod.VNPAY) {
                createdOrder.setPaymentStatus(PaymentStatus.NOT_PAID);
                createdOrder.setOrderStatus(OrderStatus.PENDING);
            }
            else {
                throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ");
            }
//            // Lưu đơn hàng vào cơ sở dữ liệu
            Order savedOrder = orderRepository.save(createdOrder);
            orders.add(savedOrder);

            // Lưu các sản phẩm trong giỏ hàng thành OrderItem
            List<OrderItem> orderItems= new ArrayList<>();
            for (CartItem item : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setProduct(item.getProduct());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSize(item.getSize());
                orderItem.setUserId(item.getUserId());
                orderItem.setDiscountPrice(item.getDiscountPrice());
                savedOrder.getOderItems().add(orderItem);

                OrderItem savedOrderItem= orderItemRepository.save(orderItem);
                orderItems.add(savedOrderItem);

            }
//            cart.getCartItems().clear();
//            cartRepository.save(cart);
        }
        return orders;
    }

    @Override
    public Order findOrderById(long id) throws Exception {
        return orderRepository.findById(id).orElseThrow(() -> new Exception("Không tìm thấy đơn hàng"));
    }

    @Override
    public List<Order> userOrderHistory(long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getAllOrders(long userId) {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrderStatus(long orderId, OrderStatus orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderStatus);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(long orderId, User user) throws Exception {
        Order order = findOrderById(orderId);

        if(!user.getId().equals(order.getUser().getId())) {
            throw new Exception("Bạn không có quyền truy cập vào đơn hàng này");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public OrderItem getOrderItemById(long id) throws Exception {
        return orderItemRepository.findById(id).orElseThrow(()-> new Exception("Đơn hàng không tồn tại..."));
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
