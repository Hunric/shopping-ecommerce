package com.ecommerce.exception; // 替换为您的实际包名

/**
 * 通用业务异常基类 (可选，但有助于统一处理)
 * 可以继承自 RuntimeException，这样就不强制在方法签名中声明 throws
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

// 可以根据需要添加更多自定义异常，例如：
// - InvalidCredentialsException (登录凭证无效)
// - InsufficientStockException (库存不足)
// - OrderProcessingException (订单处理错误)
// - UnauthorizedAccessException (无权访问)
