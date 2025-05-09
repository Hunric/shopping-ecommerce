package com.ecommerce.product.repository;

import com.ecommerce.product.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<ProductDocument, Long> {
    Page<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
    Page<ProductDocument> findByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductDocument> findByNameContainingOrDescriptionContainingAndCategoryId(
            String name, String description, Long categoryId, Pageable pageable);
} 