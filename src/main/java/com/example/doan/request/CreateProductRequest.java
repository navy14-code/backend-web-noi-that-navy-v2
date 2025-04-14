package com.example.doan.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateProductRequest {
    private String title;
    private String description;
    private int price;
    private int discountPrice;
    private String size;
    private String brand;
    private String category;
    private String category2;
    private String category3;
    private List<String> images;
}
