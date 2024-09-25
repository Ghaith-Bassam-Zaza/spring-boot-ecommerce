package com.backend.ecommerce_backend.api.controller.Products;

import com.backend.ecommerce_backend.model.Product;
import com.backend.ecommerce_backend.service.ProductService;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductsController {
    ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }
    @RequestMapping
    public List<Product> getProducts() {
        return productService.getProducts();
    }
}
