package com.example.doan.modal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;

    @OneToOne
    private User user;

    private Long totalEarnings= 0L; // Tổng doanh thu đã thanh toán

    private Long totalSales = 0L; //Tổng doanh số bán hàng

    private Long totalRefunds= 0L; // Tổng tiền hoàn

    private Long netEarnings= 0L; // Doanh thu ròng totalEarnings - totalRefunds

    private Integer totalOrders= 0; // tổng số đơn hàng đã được tạo.

    private Integer canceledOrders= 0; // Tổng đơn hàng bị hủy

    private Integer totalCustomers = 0;  // Số lượng người dùng là khách hàng

    private Integer totalTransactions= 0;  //Tổng số giao dịch đã xử lý


}
