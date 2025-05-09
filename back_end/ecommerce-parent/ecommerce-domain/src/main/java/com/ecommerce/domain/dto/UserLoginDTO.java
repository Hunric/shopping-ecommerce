package com.ecommerce.domain.dto; // 替换为您的实际包名

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录数据传输对象 (DTO)
 * 用于接收前端传递的登录凭证
 */
@Data
public class UserLoginDTO {

    /**
     * 登录凭证 (可以是用户名、邮箱或手机号)
     * - 不能为空白字符串
     */
    @NotBlank(message = "登录凭证不能为空")
    private String credential; // 使用一个通用字段接收，具体是哪种凭证由 Service 层判断

    /**
     * 密码
     * - 不能为空白字符串
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    // (可选) 记住我 功能
    // private boolean rememberMe;
}
