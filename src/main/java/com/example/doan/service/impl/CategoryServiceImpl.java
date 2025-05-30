package com.example.doan.service.impl;

import com.example.doan.modal.Category;
import com.example.doan.repository.CategoryRepository;
import com.example.doan.request.CategoryRequest;
import com.example.doan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryRequest categoryRequest) {
     Category category = new Category();
     category.setName(categoryRequest.getName());
     category.setLevel(categoryRequest.getLevel());
     category.setCategoryId(categoryRequest.getCategoryId());
     if(categoryRequest.getParentId() != null){
         Category parent = categoryRepository.findById(categoryRequest.getParentId()).orElse(null);
         category.setParentCategory(parent);
     }
        return categoryRepository.save(category);
    }


    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

}
