package com.example.doan.modal;

import com.example.doan.doman.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name ="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;

    private String orderId;

    @ManyToOne
    private User user;

    private long sellerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> oderItems = new ArrayList<>();

    @ManyToOne
    private Address shippingAddress;

    private double discountPrice;

    private Integer price;

    private Integer discount;

    private OrderStatus orderStutus;

    private int totalItem;


    private LocalDateTime orderDate = LocalDateTime.now();

    private LocalDateTime deliverDate = orderDate.plusDays(7);

}
