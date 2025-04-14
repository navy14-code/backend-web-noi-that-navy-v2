package com.example.doan.repository;

import com.example.doan.modal.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

    Category findByCategoryId(String categoryId);
}
