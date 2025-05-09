package com.ecommerce.controller; // 替换为您的实际包名

import com.ecommerce.common.ApiResponse; // 导入统一响应体 (假设在 common 模块定义)
import com.ecommerce.domain.dto.UserLoginDTO;
import com.ecommerce.domain.dto.UserRegisterDTO;
import com.ecommerce.domain.entity.User;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation; // OpenAPI 注解
import io.swagger.v3.oas.annotations.tags.Tag;    // OpenAPI 注解
import jakarta.validation.Valid; // JSR-303 Bean Validation 注解
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated; // 启用方法参数校验
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * 处理用户注册和登录相关的 API 请求
 */
@RestController // 标记为 RESTful 控制器，自动将返回对象序列化为 JSON
@RequestMapping("/api/v1/auth") // 定义此控制器处理的请求的基础路径
@Tag(name = "Authentication", description = "用户认证相关接口") // OpenAPI 文档标签
@Validated // 启用对方法参数中 @Valid 注解的校验处理
public class AuthController {

    private final UserService userService;
    // TODO: 后续需要注入 AuthenticationManager 和 JWT 工具类用于登录

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     * @param userRegisterDTO 包含注册信息的 DTO (使用 @Valid 触发校验)
     * @return 标准响应体，包含注册成功的用户信息 (不含密码)
     */
    @PostMapping("/register") // 处理 POST /api/v1/auth/register 请求
    @Operation(summary = "用户注册", description = "根据提供的信息创建新用户账户") // OpenAPI 文档描述
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        // @Valid: 告诉 Spring 对 userRegisterDTO 对象进行校验 (基于 DTO 中的注解)
        // @RequestBody: 将请求体中的 JSON 数据绑定到 userRegisterDTO 对象

        // 调用 Service 层处理注册逻辑
        User registeredUser = userService.register(userRegisterDTO);

        // 封装成功响应
        // 假设 ApiResponse 是我们定义的统一响应类，包含 code, message, data
        // 假设 ApiResponse.success(data) 创建一个成功的响应实例
        ApiResponse<User> response = ApiResponse.success(registeredUser, "注册成功");

        // 返回 HTTP 状态码 201 (Created) 和响应体
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

        // 注意：实际项目中，注册成功后通常不会直接返回 User 实体，
        // 可能会返回一个简单的成功消息，或者一个用于后续操作的 token。
        // 这里为了演示，返回了 User 对象（已清除密码）。
        // 异常处理：如果 service.register 抛出异常 (如用户名已存在)，
        // 会被全局异常处理器捕获并返回相应的错误响应。
    }

    /**
     * 用户登录接口 (基础实现 - 待完善)
     * @param userLoginDTO 包含登录凭证的 DTO
     * @return 包含认证令牌 (JWT) 的响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用凭证进行身份验证并获取令牌")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        // TODO: 实现登录逻辑
        // 1. 使用 Spring Security 的 AuthenticationManager 进行身份验证
        //    - 将 userLoginDTO 中的 credential 和 password 封装成 Authentication 对象 (如 UsernamePasswordAuthenticationToken)
        //    - 调用 authenticationManager.authenticate(authenticationToken)
        //    - 如果认证失败，会抛出 AuthenticationException，由全局异常处理器处理
        // 2. 如果认证成功，获取认证后的 Principal (通常是 UserDetails)
        // 3. 使用 JWT 工具类生成 JWT Token
        // 4. 将 Token 封装在 ApiResponse 中返回给前端

        // 临时响应，表示功能待实现
        ApiResponse<String> response = ApiResponse.success("Login endpoint - To be implemented", "登录接口待实现");
        return ResponseEntity.ok(response);
    }

}
