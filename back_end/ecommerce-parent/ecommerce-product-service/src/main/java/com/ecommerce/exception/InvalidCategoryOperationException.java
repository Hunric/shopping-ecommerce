package com.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当尝试执行无效的分类操作时抛出
 * (例如：删除有子分类或商品的分类，将分类设置为自身的父分类等)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Suggests 400 Bad Request status
public class InvalidCategoryOperationException extends BusinessException {
    public InvalidCategoryOperationException(String message) {
        super(message);
    }
}