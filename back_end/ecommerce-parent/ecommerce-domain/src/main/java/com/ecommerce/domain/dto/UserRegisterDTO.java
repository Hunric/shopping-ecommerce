package com.ecommerce.domain.dto; // 替换为您的实际包名

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册数据传输对象 (DTO)
 * 用于接收前端传递的注册信息
 */
@Data // Lombok: 自动生成 getter, setter, toString, equals, hashCode
public class UserRegisterDTO {

    /**
     * 用户名
     * - 不能为空白字符串
     * - 长度必须在 5 到 20 之间 (与 SRS UC-01 [Source 44] 校验规则对应)
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名长度必须在 5 到 20 位之间")
    private String username;

    /**
     * 电子邮箱
     * - 不能为空白字符串
     * - 必须符合 Email 格式
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入有效的邮箱地址")
    private String email;

    /**
     * 密码
     * - 不能为空白字符串
     * - 长度至少为 8 位 (与 SRS UC-01 [Source 44] 密码复杂度要求对应，更复杂的校验可以在 Service 层实现)
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少需要 8 位")
    private String password;

    /**
     * 确认密码
     * - 不能为空白字符串
     * - 确认密码与密码是否一致的校验通常在 Service 层或 Controller 层进行，DTO 只负责接收字段
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    // 注意：根据 SRS UC-01 [Source 44]，还需要校验 "同意用户协议"，但这通常通过前端复选框控制，
    // 不一定需要放在 DTO 中，可以在 Controller 层检查相关参数。
}
