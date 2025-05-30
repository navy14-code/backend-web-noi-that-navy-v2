package com.example.doan.service;

import com.example.doan.exceptions.DataNotFoundException;
import com.example.doan.exceptions.ProductException;
import com.example.doan.modal.Product;
import com.example.doan.modal.User;
import com.example.doan.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductRequest request, User user) throws DataNotFoundException;

    Product updateProduct(Long productId, Product product) throws ProductException;

    void deleteProduct(Long productId) throws ProductException;

    Product findProductById(Long productId) throws ProductException;


    List<Product> searchProduct(String query);

    Page<Product> getAllProduct(
            String category,
            String brand,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber

    );
}
