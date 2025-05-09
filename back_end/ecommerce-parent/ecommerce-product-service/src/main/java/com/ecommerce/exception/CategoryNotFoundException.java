package com.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当根据 ID 未找到商品分类时抛出
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Suggests 404 Not Found status
public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
