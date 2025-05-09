package com.ecommerce.product.service;

import com.ecommerce.domain.product.Product;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ProductSearchService {
    void saveProduct(Product product);
    void deleteProduct(Long id);
    Page<Product> search(String keyword, Long categoryId, int pageNum, int pageSize);
    void syncProductsToEs(List<Product> products);
} 