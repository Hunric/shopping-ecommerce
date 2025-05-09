package com.ecommerce.common; // 替换为您的实际包名

import lombok.Data;

/**
 * 通用 API 响应体结构
 *
 * @param <T> 响应数据的类型
 */
@Data
public class ApiResponse<T> {

    /**
     * 业务状态码 (0 表示成功，非 0 表示特定业务错误)
     */
    private int code;

    /**
     * 响应消息 (成功时通常为 "Success" 或具体操作消息，失败时为错误描述)
     */
    private String message;

    /**
     * 响应数据 (泛型，可以是任意类型)
     */
    private T data;

    // --- 构造函数 ---
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // --- 静态工厂方法 (方便创建响应对象) ---

    /**
     * 创建成功的响应 (无数据)
     *
     * @return ApiResponse<Void>
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "Success", null);
    }

    /**
     * 创建成功的响应 (带数据)
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "Success", data);
    }

    /**
     * 创建成功的响应 (带数据和自定义消息)
     *
     * @param data    响应数据
     * @param message 自定义成功消息
     * @param <T>     数据类型
     * @return ApiResponse<T>
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(0, message, data);
    }

    /**
     * 创建失败的响应 (使用预定义的错误码和消息)
     * @param errorCode 错误码枚举或对象 (需要包含 code 和 message)
     * @return ApiResponse<Void>
     */
    // public static ApiResponse<Void> fail(ErrorCode errorCode) {
    //    return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    // }
    // TODO: 后续需要定义 ErrorCode 枚举或类

    /**
     * 创建失败的响应 (自定义错误码和消息)
     *
     * @param code    错误码
     * @param message 错误消息
     * @return ApiResponse<Void>
     */
    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

}
