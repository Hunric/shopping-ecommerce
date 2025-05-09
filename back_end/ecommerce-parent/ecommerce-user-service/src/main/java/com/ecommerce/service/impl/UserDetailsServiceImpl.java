package com.ecommerce.service.impl; // 或放在 security 包下

import com.ecommerce.domain.entity.User;
import com.ecommerce.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList; // 用于创建空的权限列表

/**
 * 实现 Spring Security 的 UserDetailsService
 * 用于根据用户名从数据库加载用户信息
 */
@Service // 标记为 Spring 服务组件
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 根据用户名加载用户详细信息
     * @param username 在我们的登录逻辑中，这里传入的可能是用户名、邮箱或手机号 (即 UserLoginDTO 中的 credential)
     * @return UserDetails 对象，包含用户名、密码哈希、权限等信息
     * @throws UsernameNotFoundException 如果用户未找到
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 尝试根据 credential (传入的 username 参数) 查找用户
        // 这里需要根据实际情况判断 credential 是用户名、邮箱还是手机号
        // 简单起见，我们先假设它就是用户名
        // TODO: 后续可以优化为同时尝试邮箱和手机号查找
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 将我们自己定义的 User 实体 转换为 Spring Security 需要的 UserDetails 对象
        // Spring Security 提供了一个内置的 UserDetails 实现：org.springframework.security.core.userdetails.User
        // 参数：用户名, 密码哈希, 账户是否启用, 账户是否未过期, 凭证是否未过期, 账户是否未锁定, 权限列表
        // 注意：我们的 User 实体中目前只有启用状态 (status)，其他的暂时设为 true
        // 权限列表 (authorities) 我们暂时设为空，后续可以根据角色/权限系统来填充
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),             // 用户名
                user.getPasswordHash(),         // 数据库中存储的密码哈希
                user.getStatus() == 1,          // 账户是否启用 (假设 status=1 表示启用)
                true,                      // 账户是否未过期 (暂设为 true)
                true,                      // 凭证是否未过期 (暂设为 true)
                true,                      // 账户是否未锁定 (暂设为 true)
                new ArrayList<>()             // 用户权限列表 (暂为空)
        );
    }
}
