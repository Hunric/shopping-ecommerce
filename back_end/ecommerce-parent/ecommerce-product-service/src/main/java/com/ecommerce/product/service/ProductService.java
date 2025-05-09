package com.ecommerce.product.service;

import com.ecommerce.domain.product.Product;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    List<Product> listProducts(Integer pageNum, Integer pageSize);
    List<Product> searchProducts(String keyword, Long categoryId, Integer pageNum, Integer pageSize);
    void updateStock(Long productId, Integer stock);
    void updateStatus(Long productId, Integer status);
} 