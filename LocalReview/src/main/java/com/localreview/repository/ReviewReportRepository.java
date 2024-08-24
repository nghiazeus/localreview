package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.ReviewReports;

@Repository 
public interface ReviewReportRepository extends JpaRepository<ReviewReports, String> {
    // Bạn có thể định nghĩa các phương thức truy vấn tùy chỉnh tại đây nếu cần
}