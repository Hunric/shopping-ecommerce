package com.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当商品库存不足时抛出
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Suggests 400 Bad Request
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message);
    }
}