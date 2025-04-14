package com.example.doan.repository;

import com.example.doan.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository  extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    @Query("SELECT p FROM Product p " + "WHERE (:query IS NULL OR LOWER(p.title) " +
            "LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "OR (:query IS NULL OR LOWER(p.category.name) " +
            "LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProduct(@Param("query") String query);

}
