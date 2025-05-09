package com.ecommerce.user.service;

import com.ecommerce.domain.user.User;

public interface UserService {
    User register(String username, String password, String email);
    User login(String username, String password);
    User getUserById(Long id);
    void updateUser(User user);
    void changePassword(Long userId, String oldPassword, String newPassword);
} 