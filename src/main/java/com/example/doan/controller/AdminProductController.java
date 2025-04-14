package com.example.doan.controller;

import com.example.doan.exceptions.ProductException;
import com.example.doan.exceptions.UserException;
import com.example.doan.modal.Product;
import com.example.doan.modal.User;
import com.example.doan.request.CreateProductRequest;
import com.example.doan.service.ProductService;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductService productService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Product> creteProduct(
            @RequestBody CreateProductRequest request,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user = userService.getUserProfile(jwt);

        Product product = productService.createProduct(request, user);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProductException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,@RequestBody Product product)
            throws ProductException {
        Product updateProduct = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updateProduct,HttpStatus.OK);
    }
}
