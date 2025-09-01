package com.hunric.model.dto;

import lombok.Data;

/**
 * 通用响应结果数据传输对象
 *
 * @param <T> 数据类型
 */
@Data
public class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = code == 200;
    }
    
    /**
     * 成功响应（无数据）
     *
     * @return 响应结果
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "操作成功", null);
    }
    
    /**
     * 成功响应（有数据）
     *
     * @param data 数据
     * @return 响应结果
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应（自定义消息和数据）
     *
     * @param message 消息
     * @param data    数据
     * @return 响应结果
     */
    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(200, message, data);
    }
    
    /**
     * 失败响应
     *
     * @param code    状态码
     * @param message 消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }
    
    /**
     * 参数错误响应
     *
     * @param message 消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> badRequest(String message) {
        return new ResponseResult<>(400, message, null);
    }
    
    /**
     * 未授权响应
     *
     * @param message 消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> unauthorized(String message) {
        return new ResponseResult<>(401, message, null);
    }
    
    /**
     * 禁止访问响应
     *
     * @param message 消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> forbidden(String message) {
        return new ResponseResult<>(403, message, null);
    }
    
    /**
     * 资源不存在响应
     *
     * @param message 消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> notFound(String message) {
        return new ResponseResult<>(404, message, null);
    }
    
    /**
     * 服务器错误响应
     *
     * @param message 消息
     * @return 响应结果
     */
    public static <T> ResponseResult<T> serverError(String message) {
        return new ResponseResult<>(500, message, null);
    }
} 