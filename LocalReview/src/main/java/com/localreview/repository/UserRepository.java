package com.localreview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
    // Bạn có thể định nghĩa các phương thức truy vấn tùy chỉnh tại đây nếu cần
    User findByEmail(String email);
    User findByUserId(String userId); // Tìm theo user_id kiểu String
    
    User findByGoogleId(String googleId);
    
    
}
