package com.backend.ecommerce_backend.model.dao;

import com.backend.ecommerce_backend.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductRepo extends ListCrudRepository<Product, Long> {
}
