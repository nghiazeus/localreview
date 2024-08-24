package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    // Bạn có thể định nghĩa các phương thức truy vấn tùy chỉnh tại đây nếu cần
 
}