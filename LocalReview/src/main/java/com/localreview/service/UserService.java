package com.localreview.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.localreview.entity.User;
import com.localreview.repository.UserRepository;

public interface UserService {
	

    User findByEmail(String email);
    User findByUserId(String userId); // Thay đổi để tìm theo user_id kiểu String
    User saveUser(User user);
    User findOrCreateUser(String email, String name, String googleId);
    
    User getUserById(String userId);
    
    User getUserByEmail(String email);
    
    
}