package com.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当尝试创建已存在的用户或邮箱时抛出
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // 建议关联一个合适的 HTTP 状态码
public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
