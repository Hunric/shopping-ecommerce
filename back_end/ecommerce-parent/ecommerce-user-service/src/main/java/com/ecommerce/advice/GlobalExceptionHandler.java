package com.ecommerce.advice; // 替换为您的实际包名

import com.ecommerce.common.ApiResponse; // 导入统一响应体
import com.ecommerce.exception.PasswordMismatchException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j; // 导入 Slf4j 日志注解
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 捕获 Controller 层抛出的异常，并返回统一的响应格式
 */
@RestControllerAdvice // 组合了 @ControllerAdvice 和 @ResponseBody
@Slf4j // Lombok 注解，自动生成 log 实例，方便记录日志
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常：用户已存在
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置 HTTP 状态码为 400
    public ApiResponse<Void> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.warn("业务异常 - 用户已存在: {}", ex.getMessage()); // 记录警告日志
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getMessage()); // 返回统一错误响应
    }

    /**
     * 处理自定义的业务异常：资源未找到
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 设置 HTTP 状态码为 404
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("业务异常 - 资源未找到: {}", ex.getMessage());
        return ApiResponse.fail(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    /**
     * 处理自定义的业务异常：密码不匹配
     */
    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置 HTTP 状态码为 400
    public ApiResponse<Void> handlePasswordMismatchException(PasswordMismatchException ex) {
        log.warn("业务异常 - 密码不匹配: {}", ex.getMessage());
        return ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * 处理 JSR-303 Bean Validation 校验失败异常
     * 当 Controller 方法参数使用了 @Valid 且校验失败时触发
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("参数校验失败: {}", errors);

        // 直接创建包含错误 Map 的 ApiResponse 对象
        // 不需要再调用 setData，因为构造函数已经设置了 data
        // response.setData(errors); // 这行可以删除

        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), // code
                "参数校验失败",                 // message
                errors                         // data (包含详细错误)
        );
    }

    /**
     * 处理其他未被特定捕获的运行时异常 (作为最后的防线)
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置 HTTP 状态码为 500
    public ApiResponse<Void> handleGenericRuntimeException(RuntimeException ex) {
        log.error("未捕获的运行时异常: ", ex); // 记录错误日志，包含堆栈信息
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请联系管理员");
    }

    /**
     * 处理所有其他 Exception (更广泛的捕获)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleAllExceptions(Exception ex) {
        log.error("未处理的异常: ", ex);
        return ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器发生未知错误");
    }
}
