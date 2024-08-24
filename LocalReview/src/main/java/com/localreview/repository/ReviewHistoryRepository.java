package com.localreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.ReviewHistory;

@Repository
public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, String> {
    // Bạn có thể định nghĩa các phương thức truy vấn tùy chỉnh tại đây nếu cần
}