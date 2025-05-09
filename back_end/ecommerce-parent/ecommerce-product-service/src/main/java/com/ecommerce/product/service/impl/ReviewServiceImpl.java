package com.ecommerce.product.service.impl;

import com.ecommerce.domain.product.Review;
import com.ecommerce.product.mapper.ReviewMapper;
import com.ecommerce.product.service.ReviewService;
import com.ecommerce.product.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final String CACHE_KEY_REVIEW = "review:";
    private static final String CACHE_KEY_PRODUCT_REVIEWS = "product:reviews:";
    private static final String CACHE_KEY_USER_REVIEWS = "user:reviews:";

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    @Transactional
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public Review createReview(Review review) {
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        review.setStatus(0); // 默认待审核
        reviewMapper.insert(review);
        return review;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public Review updateReview(Review review) {
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.update(review);
        return review;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void deleteReview(Long id) {
        reviewMapper.delete(id);
    }

    @Override
    @Cacheable(value = "reviews", key = "'detail:' + #id")
    public Review getReviewById(Long id) {
        return reviewMapper.findById(id);
    }

    @Override
    @Cacheable(value = "reviews", key = "'product:' + #productId + ':' + #pageNum + ':' + #pageSize")
    public List<Review> getProductReviews(Long productId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return reviewMapper.findByProductId(productId, offset, pageSize);
    }

    @Override
    @Cacheable(value = "reviews", key = "'user:' + #userId + ':' + #pageNum + ':' + #pageSize")
    public List<Review> getUserReviews(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return reviewMapper.findByUserId(userId, offset, pageSize);
    }

    @Override
    @Cacheable(value = "reviews", key = "'rating:' + #productId")
    public Double getProductAverageRating(Long productId) {
        return reviewMapper.calculateAverageRating(productId);
    }

    @Override
    @Cacheable(value = "reviews", key = "'count:' + #productId")
    public Long getProductReviewCount(Long productId) {
        return reviewMapper.countByProductId(productId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviews"}, allEntries = true)
    public void updateReviewStatus(Long id, Integer status) {
        reviewMapper.updateStatus(id, status);
    }
} 