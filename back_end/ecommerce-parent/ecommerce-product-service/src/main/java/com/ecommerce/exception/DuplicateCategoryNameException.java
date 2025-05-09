package com.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当尝试创建或更新的分类名称在同级下已存在时抛出
 */
@ResponseStatus(HttpStatus.CONFLICT) // Suggests 409 Conflict status
public class DuplicateCategoryNameException extends BusinessException {
    public DuplicateCategoryNameException(String message) {
        super(message);
    }
}