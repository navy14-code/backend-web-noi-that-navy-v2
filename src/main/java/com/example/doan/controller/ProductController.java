package com.example.doan.controller;

import com.example.doan.exceptions.ProductException;
import com.example.doan.modal.Product;
import com.example.doan.modal.User;
import com.example.doan.request.CreateProductRequest;
import com.example.doan.service.ProductService;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/{productId}")
    public ResponseEntity<Product> findProductById(@PathVariable Long productId) throws ProductException {
        Product product = productService.findProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String query) {
        List<Product> products = productService.searchProduct(query);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String sizes,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String stock,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        return new ResponseEntity<>(
                productService.getAllProduct(category, brand,
                        sizes, minPrice, maxPrice,
                        minDiscount, sort, stock,
                        pageNumber), HttpStatus.OK);
    }

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
