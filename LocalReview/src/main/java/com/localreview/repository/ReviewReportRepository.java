package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.localreview.entity.ReviewReports;
import com.localreview.entityEnum.ReportStatus;
import org.springframework.transaction.annotation.Transactional;

@Repository 
public interface ReviewReportRepository extends JpaRepository<ReviewReports, String> {
    // Tìm tất cả các báo cáo theo trạng thái
    List<ReviewReports> findByStatus(ReportStatus status);
    
    // Kiểm tra xem báo cáo đã tồn tại dựa trên reviewId và reportedBy
    boolean existsByReviewIdAndReportedBy_UserId(String reviewId, String reportedById);
    
    // Đếm số lượng báo cáo cho userId và còn hoạt động
    long countByReportedUserId_UserIdAndActive(String reportedUserId, boolean active);
    
    @Modifying
    @Transactional
    @Query("UPDATE ReviewReports rr SET rr.active = false WHERE rr.reportedUserId.userId = :userId")
    void deactivateReportsByUserId(@Param("userId") String userId);
}
