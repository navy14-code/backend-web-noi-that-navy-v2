package com.example.doan.service;

import com.example.doan.modal.Category;
import com.example.doan.request.CategoryRequest;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(CategoryRequest categoryRequest);
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
//    Category updateCategoryTree(String cat1Id, String cat1Name,
//                                String cat2Id, String cat2Name,
//                                String cat3Id, String cat3Name);

}
