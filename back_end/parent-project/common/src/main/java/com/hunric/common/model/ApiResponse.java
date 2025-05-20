package com.hunric.common.model;

import java.io.Serializable;

/**
 * 通用API响应对象
 * @param <T> 响应数据类型
 */
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 构造成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "操作成功");
    }
    
    /**
     * 构造成功响应
     * @param data 响应数据
     * @param message 响应消息
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, true);
    }
    
    /**
     * 构造错误响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, 500);
    }
    
    /**
     * 构造错误响应
     * @param message 错误消息
     * @param code 错误码
     * @param <T> 数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(code, message, null, false);
    }
    
    /**
     * 私有构造函数
     */
    private ApiResponse(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }
    
    /**
     * 默认构造函数
     */
    public ApiResponse() {
    }
    
    // Getter和Setter
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
} 