package com.localreview.serviceiml;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import com.localreview.entity.Blacklist;
import com.localreview.entity.CustomUserDetails; // Đảm bảo dòng này đã có
import com.localreview.DTO.ReviewReportDTO;
import com.localreview.entity.Review;
import com.localreview.entity.ReviewReports;
import com.localreview.entity.User;
import com.localreview.entityEnum.ReportStatus;
import com.localreview.repository.BlacklistRepository;
import com.localreview.repository.ReviewReportRepository;
import com.localreview.repository.ReviewRepository;
import com.localreview.service.ReviewReportService;
import com.localreview.service.UserService;

@Service
public class ReviewReportServiceImpl implements ReviewReportService {

    @Autowired
    private ReviewReportRepository reviewReportRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BlacklistRepository blacklistRepository; // Inject BlacklistRepository

    // Cập nhật phương thức để nhận ReviewReportDTO
    @Override
    public ReviewReports reportReview(ReviewReportDTO reportDTO) {
        // Kiểm tra lý do báo cáo có hợp lệ không
        if (reportDTO.getReason() == null || reportDTO.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Bạn cần chọn một lý do để báo cáo.");
        }

        // Tìm kiếm đánh giá theo reviewId
        Review review = reviewRepository.findById(reportDTO.getReviewId()).orElse(null);
        
        if (review == null) {
            throw new IllegalArgumentException("Review not found for ID: " + reportDTO.getReviewId());
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reportedBy;

        // Kiểm tra kiểu của principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) principal;
            String googleId = oauthUser.getAttribute("sub"); // Lấy ID người dùng từ Google (userId)

            // Tìm người dùng trong cơ sở dữ liệu bằng googleId
            reportedBy = userService.findByGoogleId(googleId); // Giả sử có phương thức này trong UserService
            if (reportedBy == null) {
                throw new IllegalArgumentException("Người dùng không tồn tại trong cơ sở dữ liệu.");
            }
        } else if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            reportedBy = userService.findByUserId(userDetails.getUserId()); // Lấy đối tượng User từ UserDetails
        } else {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }

        // Kiểm tra xem người dùng đã báo cáo người dùng này chưa
        if (reviewReportRepository.existsByReviewIdAndReportedBy_UserId(reportDTO.getReviewId(), reportedBy.getUserId())) {
            throw new IllegalArgumentException("Bạn đã báo cáo người dùng này rồi.");
        }
        
        // Lấy reportedUserId từ đối tượng Review
        User reportedUser = review.getUser(); // Lấy đối tượng User đã viết đánh giá
        
        // Tạo đối tượng báo cáo
        ReviewReports report = new ReviewReports();
        report.setReviewId(reportDTO.getReviewId());
        report.setReportedUser(reportedUser.getUserId()); // Sử dụng đối tượng User từ đánh giá
        report.setReportedByuser(reportedBy.getUserId()); // Gán người báo cáo
        report.setReason(reportDTO.getReason()); // Nhận lý do từ DTO
        report.setReportedAt(LocalDateTime.now()); // Đặt thời gian báo cáo
        report.setStatus(ReportStatus.PENDING); // Đặt trạng thái báo cáo

        // Lưu báo cáo vào cơ sở dữ liệu
        ReviewReports savedReport = reviewReportRepository.save(report);

        // Cập nhật số lượng báo cáo cho người dùng bị báo cáo
        long reportCount = reviewReportRepository.countByReportedUserId_UserIdAndActive(reportedUser.getUserId(), true);
        
        // Nếu số lượng báo cáo đạt 2, thêm người dùng vào Blacklist
        if (reportCount >= 2) {
            // Tạo đối tượng Blacklist
            Blacklist blacklistEntry = new Blacklist();
            blacklistEntry.setUser(reportedUser); // Đặt người dùng vào blacklist
            blacklistEntry.setReason("Người dùng này đã bị báo cáo 5 lần");
            blacklistEntry.setCreatedAt(LocalDateTime.now());
            blacklistEntry.setExpiresAt(LocalDateTime.now().plusSeconds(60)); // Ví dụ: hết hạn sau 30 ngày
            
            // Lưu vào bảng Blacklist
            blacklistRepository.save(blacklistEntry);
        }

        return savedReport;
    }

    @Override
    public Optional<ReviewReports> getReportById(String reportId) {
        return reviewReportRepository.findById(reportId);
    }

    @Override
    public List<ReviewReports> getAllReports() {
        return reviewReportRepository.findAll();
    }
}
