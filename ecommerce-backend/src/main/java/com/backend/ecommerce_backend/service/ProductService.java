package com.backend.ecommerce_backend.service;

import com.backend.ecommerce_backend.model.Product;
import com.backend.ecommerce_backend.model.dao.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }
    public List<Product> getProducts() {
        return productRepo.findAll();
    }
}
