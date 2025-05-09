package com.ecommerce.product.mapper;

import com.ecommerce.domain.product.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReviewMapper {
    void insert(Review review);
    void update(Review review);
    void delete(@Param("id") Long id);
    Review findById(@Param("id") Long id);
    List<Review> findByProductId(@Param("productId") Long productId, 
                                @Param("offset") Integer offset, 
                                @Param("limit") Integer limit);
    List<Review> findByUserId(@Param("userId") Long userId, 
                             @Param("offset") Integer offset, 
                             @Param("limit") Integer limit);
    Double calculateAverageRating(@Param("productId") Long productId);
    Long countByProductId(@Param("productId") Long productId);
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 