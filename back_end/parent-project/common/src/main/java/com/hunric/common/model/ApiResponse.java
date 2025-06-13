package com.hunric.common.model;

import java.io.Serializable;

/**
 * 电商平台通用API响应封装类
 * 
 * @description 统一的API响应数据结构，用于封装所有API接口的返回结果。
 *              提供标准化的响应格式，包括状态码、消息、数据和成功标识。
 *              支持泛型，可以封装任意类型的响应数据。
 * 
 * @features
 * - 泛型支持，可封装任意类型数据
 * - 标准化的响应结构
 * - 静态工厂方法，便于创建响应对象
 * - 序列化支持，便于网络传输
 * - 成功/失败状态明确标识
 * - 灵活的错误码和消息定制
 * 
 * @response_structure
 * - code: HTTP状态码或业务状态码
 * - message: 响应消息或错误描述
 * - data: 实际响应数据（泛型）
 * - success: 操作是否成功的布尔标识
 * 
 * @usage_examples
 * <pre>
 * // 成功响应
 * ApiResponse<User> response = ApiResponse.success(user);
 * ApiResponse<List<Product>> response = ApiResponse.success(products, "查询成功");
 * 
 * // 错误响应
 * ApiResponse<Void> response = ApiResponse.error("参数错误");
 * ApiResponse<Void> response = ApiResponse.error("权限不足", 403);
 * </pre>
 * 
 * @status_codes
 * - 200: 操作成功
 * - 400: 请求参数错误
 * - 401: 未授权访问
 * - 403: 权限不足
 * - 404: 资源不存在
 * - 500: 服务器内部错误
 * 
 * @serialization
 * 实现Serializable接口，支持序列化和反序列化，
 * 便于在分布式系统中进行数据传输和缓存存储。
 * 
 * @thread_safety
 * 该类是不可变的（通过私有构造函数和静态工厂方法），
 * 因此是线程安全的。
 * 
 * @param <T> 响应数据的类型
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link java.io.Serializable} 序列化接口
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