package com.example.doan.modal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "coupen")
public class Coupen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;


    private String code; // mã giảm giá

    private String discountPercentage; //% giảm giá

    private LocalDate validityStartDate; // ngày bắt đầu

    private LocalDate validityEndDate; // ngày kết thúc

    private double minimumOderValue; // giá tiếu thiểu

    private boolean isActive= true;

    @ManyToMany(mappedBy = "usedCoupons")
    private Set<User> usedByUsers = new HashSet<>();
}
