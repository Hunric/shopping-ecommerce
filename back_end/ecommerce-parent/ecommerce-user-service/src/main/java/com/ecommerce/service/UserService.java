package com.ecommerce.service; // 替换为您的实际包名

import com.ecommerce.domain.dto.UserRegisterDTO;
import com.ecommerce.domain.entity.User; // 导入 User 实体类
import com.ecommerce.exception.PasswordMismatchException;
import com.ecommerce.exception.UserAlreadyExistsException;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {

    /**
     * 用户注册
     * @param userRegisterDTO 包含注册信息的 DTO
     * @return 注册成功的用户实体 (通常不包含密码哈希)
     * @throws UserAlreadyExistsException 如果用户名或邮箱已存在 (自定义异常)
     * @throws PasswordMismatchException 如果两次密码不一致 (自定义异常)
     */
    User register(UserRegisterDTO userRegisterDTO);

    /**
     * 根据用户名查找用户 (用于登录验证等)
     * @param username 用户名
     * @return 用户实体，如果找不到则返回 null 或抛出异常
     */
    User findByUsername(String username);

    // 可以根据需要添加更多方法，例如：
    // User findByEmail(String email);
    // User findByPhoneNumber(String phoneNumber);
    // User getUserInfo(Long userId); // 获取用户信息
    // void updateUserInfo(Long userId, UserInfoUpdateDTO dto); // 更新用户信息
    // void changePassword(Long userId, ChangePasswordDTO dto); // 修改密码
}
