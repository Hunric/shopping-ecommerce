package com.ecommerce.order.exception; // 假设在 order 服务的 exception 包下

/**
 * 订单创建过程中发生的特定业务异常
 */
public class OrderCreationException extends RuntimeException {
    public OrderCreationException(String message) {
        super(message);
    }

    public OrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

