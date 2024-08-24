package com.localreview.service;

import com.localreview.entity.User;

public interface UserService {
    User findByEmail(String email);
    User findByUserId(String userId); // Thay đổi để tìm theo user_id kiểu String
    User saveUser(User user);
}