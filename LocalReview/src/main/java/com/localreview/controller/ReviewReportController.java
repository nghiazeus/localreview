package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import com.localreview.DTO.ReviewReportDTO;
import com.localreview.entity.CustomUserDetails;
import com.localreview.entity.ReviewReports;
import com.localreview.entity.User;
import com.localreview.entityEnum.ReportStatus;
import com.localreview.repository.UserRepository;
import com.localreview.service.ReviewReportService;
import com.localreview.service.ReviewService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/report")
public class ReviewReportController {

	 @Autowired
	    private ReviewService reviewService;
	 
	 @Autowired
	  private UserRepository userRepository;

	    @Autowired
	    private ReviewReportService reviewReportService;
	    

	    @PostMapping("/create")
	    public ResponseEntity<?> createReport(@RequestBody ReviewReportDTO reportDTO) {
	        try {
	            // Xử lý logic tạo báo cáo
	            ReviewReports createdReport = reviewReportService.reportReview(reportDTO);
	            return ResponseEntity.ok(createdReport);
	        } catch (IllegalArgumentException e) {
	            // Trả về thông báo lỗi nếu người dùng đã báo cáo
	            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
	        }
	    }

	    
	    // Phương thức để lấy ID người dùng đang đăng nhập
	    private String getCurrentUserId() {
	        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        if (principal instanceof CustomUserDetails) { // Kiểm tra xem principal có phải là CustomUserDetails không
	            return ((CustomUserDetails) principal).getUserId(); // Lấy userId từ CustomUserDetails
	        } else {
	            return null; // Nếu không tìm thấy người dùng đang đăng nhập
	        }
	    }




    @GetMapping("/{id}")
    public ResponseEntity<ReviewReports> getReport(@PathVariable String id) {
        return reviewReportService.getReportById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
