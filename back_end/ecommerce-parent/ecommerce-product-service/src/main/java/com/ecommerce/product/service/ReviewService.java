package com.ecommerce.product.service;

import com.ecommerce.domain.product.Review;
import java.util.List;

public interface ReviewService {
    Review createReview(Review review);
    Review updateReview(Review review);
    void deleteReview(Long id);
    Review getReviewById(Long id);
    List<Review> getProductReviews(Long productId, Integer pageNum, Integer pageSize);
    List<Review> getUserReviews(Long userId, Integer pageNum, Integer pageSize);
    Double getProductAverageRating(Long productId);
    Long getProductReviewCount(Long productId);
    void updateReviewStatus(Long id, Integer status);
} 