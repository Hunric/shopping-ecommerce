package com.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 当两次输入的密码不匹配时抛出 (例如，注册时)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
