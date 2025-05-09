package com.ecommerce.product.service;

public interface CacheService {
    void set(String key, Object value);
    void set(String key, Object value, long seconds);
    Object get(String key);
    void delete(String key);
    void deleteByPattern(String pattern);
} 