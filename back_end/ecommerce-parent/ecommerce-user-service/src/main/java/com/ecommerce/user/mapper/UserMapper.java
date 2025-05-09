package com.ecommerce.user.mapper;

import com.ecommerce.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
    void insert(User user);
    void update(User user);
} 