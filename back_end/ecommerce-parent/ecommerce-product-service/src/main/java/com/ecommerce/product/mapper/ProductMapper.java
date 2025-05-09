package com.ecommerce.product.mapper;

import com.ecommerce.domain.product.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    void insert(Product product);
    void update(Product product);
    void delete(@Param("id") Long id);
    Product findById(@Param("id") Long id);
    List<Product> findAll(@Param("offset") Integer offset, @Param("limit") Integer limit);
    List<Product> search(@Param("keyword") String keyword, 
                        @Param("categoryId") Long categoryId,
                        @Param("offset") Integer offset, 
                        @Param("limit") Integer limit);
    void updateStock(@Param("id") Long id, @Param("stock") Integer stock);
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 