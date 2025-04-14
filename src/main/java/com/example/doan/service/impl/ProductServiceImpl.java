package com.example.doan.service.impl;

import com.example.doan.exceptions.ProductException;
import com.example.doan.modal.Category;
import com.example.doan.modal.Product;
import com.example.doan.modal.User;
import com.example.doan.repository.CategoryRepository;
import com.example.doan.repository.ProductRepository;
import com.example.doan.request.CreateProductRequest;
import com.example.doan.service.ProductService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(CreateProductRequest req, User user) {
        Category category1 = categoryRepository.findByCategoryId(req.getCategory());
        if (category1 == null) {
            category1 = new Category();
            category1.setCategoryId(req.getCategory());
            category1.setLevel(1);
            category1 = categoryRepository.save(category1);
        }

        Category category2 = categoryRepository.findByCategoryId(req.getCategory2());
        if (category2 == null) {
            category2 = new Category();
            category2.setCategoryId(req.getCategory2());
            category2.setLevel(2);
            category2.setParentCategory(category1);
            category2 = categoryRepository.save(category2);
        }

        Category category3 = categoryRepository.findByCategoryId(req.getCategory3());
        if (category3 == null) {
            category3 = new Category();
            category3.setCategoryId(req.getCategory3());
            category3.setLevel(3);
            category3.setParentCategory(category2);
            category3 = categoryRepository.save(category3);
        }

        int discountPercentage = calculateDiscountPercent(req.getPrice(), req.getDiscountPrice());

        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setDiscountPrice(req.getDiscountPrice());
        product.setSize(req.getSize());
        product.setBrand(req.getBrand());
        product.setCategory(category3);
        product.setUser(user);
        product.setCreatedAt(LocalDateTime.now());
        product.setNumRatings(0);
        product.setImages(req.getImages()); // nếu có logic ảnh riêng thì handle thêm
        product.setDiscountPercent(discountPercentage);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) throws ProductException {
        findProductById(productId);
        product.setId(productId);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        productRepository.delete(p);
    }

    @Override
    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id " + productId));
    }

    @Override
    public List<Product> searchProduct(String query) {
        return productRepository.searchProduct(query);
    }

    @Override
    public Page<Product> getAllProduct(String category, String brand, String sizes, Integer minPrice, Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber) {
        Specification<Product> spec=(root, query, criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryId"), category));
            }
            if (brand != null && !brand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand"), brand));
            }
            if(sizes != null && !sizes.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("size"), sizes));
            }
            if(minPrice != null ) {
                predicates.add(criteriaBuilder.equal(root.get("sellingPrice"), minPrice));
            }
            if(maxPrice != null) {
                predicates.add(criteriaBuilder.equal(root.get("sellingPrice"), maxPrice));
            }
            if(minDiscount != null) {
                predicates.add(criteriaBuilder.equal(root.get("discountPercent"), minDiscount));
            }
            if (stock != null ) {
                predicates.add(criteriaBuilder.equal(root.get("stock"), stock));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = switch (sort) {
                case "price_low" -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.by("sellingPrice").ascending());
                case "price_high" -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.by("sellingPrice").descending());
                default -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10,
                        Sort.unsorted());
            };
        } else {
            pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.unsorted());
        }
        return productRepository.findAll(spec, pageable);
    }

    private int calculateDiscountPercent(int price, int discountPrice) {
        if (price <= 0) {
            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0");
        }
        double discount = price - discountPrice;
        double discountPercentage = (discount / price) * 100;
        return (int) discountPercentage;
    }

}

