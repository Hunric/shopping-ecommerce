package com.hunric.mapper;

import com.hunric.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户信息Mapper接口
 */
@Mapper
public interface UserInfoMapper {
    
    /**
     * 根据ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserInfo findById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo findByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查询用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    UserInfo findByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    UserInfo findByPhone(@Param("phone") String phone);
    
    /**
     * 根据ID查询用户名
     *
     * @param id 用户ID
     * @return 用户名
     */
    String findUsernameById(@Param("id") Long id);
    
    /**
     * 插入用户信息
     *
     * @param userInfo 用户信息
     * @return 影响行数
     */
    int insert(UserInfo userInfo);
    
    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息
     * @return 影响行数
     */
    int update(UserInfo userInfo);
} 