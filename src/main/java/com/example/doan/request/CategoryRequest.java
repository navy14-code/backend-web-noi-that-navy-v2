package com.example.doan.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private String categoryId;
    private String name;
    private Long parentId;
    private Integer level;
}
