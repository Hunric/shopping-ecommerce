package com.hunric.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 用户信息实体类
 */
@Entity
@Table(name = "user_info")
@Data
public class UserInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "gender", length = 1)
    private String gender;
    
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;
}