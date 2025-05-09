package com.ecommerce.service.impl; // 替换为您的实际包名

import com.ecommerce.domain.dto.UserRegisterDTO;
import com.ecommerce.domain.entity.User;
import com.ecommerce.exception.PasswordMismatchException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.UserService;
// 导入自定义异常 (后续需要创建这些异常类)
// import com.ecommerce.exception.UserAlreadyExistsException;
// import com.ecommerce.exception.PasswordMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 导入密码编码器
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入事务注解

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service // 标记为 Spring 服务组件
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // 注入密码编码器

    @Autowired // 构造函数注入
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // 开启事务管理，确保注册操作的原子性
    public User register(UserRegisterDTO userRegisterDTO) {
        // 1. 校验密码和确认密码是否一致
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            // 抛出自定义异常 (后续创建)
            // throw new PasswordMismatchException("两次输入的密码不一致");
            // 暂时用 RuntimeException 替代
            throw new PasswordMismatchException("两次输入的密码不一致");
        }

        // 2. 检查用户名是否已存在
        Optional<User> existingUserByUsername = userMapper.findByUsername(userRegisterDTO.getUsername());
        if (existingUserByUsername.isPresent()) {
            // 抛出自定义异常 (后续创建)
            // throw new UserAlreadyExistsException("用户名 '" + userRegisterDTO.getUsername() + "' 已被注册");
            throw new UserAlreadyExistsException("用户名 '" + userRegisterDTO.getUsername() + "' 已被注册");
        }

        // 3. 检查邮箱是否已存在
        Optional<User> existingUserByEmail = userMapper.findByEmail(userRegisterDTO.getEmail());
        if (existingUserByEmail.isPresent()) {
            // 抛出自定义异常 (后续创建)
            // throw new UserAlreadyExistsException("邮箱 '" + userRegisterDTO.getEmail() + "' 已被注册");
            throw new UserAlreadyExistsException("邮箱 '" + userRegisterDTO.getEmail() + "' 已被注册");
        }

        // 4. 创建 User 实体对象
        User newUser = new User();
        newUser.setUsername(userRegisterDTO.getUsername());
        newUser.setEmail(userRegisterDTO.getEmail());
        // 5. 对密码进行加密处理
        newUser.setPasswordHash(passwordEncoder.encode(userRegisterDTO.getPassword()));
        newUser.setStatus(1); // 默认状态为 Active
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        // phoneNumber 可以后续让用户自行补充，注册时不强制

        // 6. 调用 Mapper 将用户数据插入数据库
        userMapper.insert(newUser);

        // 7. 返回注册成功的用户对象 (通常不包含密码哈希)
        // 注意：insert 方法通过 @Options 获取了自增 ID，所以 newUser.getId() 是有值的
        newUser.setPasswordHash(null); // 清除敏感信息
        return newUser;
    }

    @Override
    public User findByUsername(String username) {
        // 调用 Mapper 查询，如果找不到返回 null (或者根据业务需求抛出异常)
        return userMapper.findByUsername(username).orElse(null);
    }

    // 实现 UserService 接口中定义的其他方法...
}
