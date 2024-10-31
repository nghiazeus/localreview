package com.localreview.Admin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.localreview.entity.ReviewReports;
import com.localreview.service.ReviewReportService;

@Controller
@RequestMapping("/admin/reports") // Đường dẫn API cho controller
public class ReviewReportControllerAdmin {
	
    private final ReviewReportService reviewReportService;

    @Autowired
    public ReviewReportControllerAdmin(ReviewReportService reviewReportService) {
        this.reviewReportService = reviewReportService;
    }
	
    // Hiển thị tất cả các báo cáo
    @GetMapping
    public String getAllReports(Model model) {
        List<ReviewReports> reports = reviewReportService.getAllReports();
        
        model.addAttribute("reportItems", reports); // Sử dụng "reportItems" để đồng bộ với template
        model.addAttribute("currentPage", 1); // Giả định trang hiện tại là 1
        model.addAttribute("totalPage", calculateTotalPages(reports.size(), 10)); // Thay 10 bằng số item mỗi trang
        return "admins/reportReview"; // Trả về tên view (reportList.html)
    }

    // Hiển thị báo cáo theo ID
    @GetMapping("/{reportId}")
    public String getReportById(@PathVariable String reportId, Model model) {
        Optional<ReviewReports> report = reviewReportService.getReportById(reportId);
        if (report.isPresent()) {
            model.addAttribute("report", report.get());
            return "reportDetail"; // Trả về tên view cho chi tiết báo cáo
        } else {
            return "notFound"; // Trả về view không tìm thấy nếu không tìm thấy
        }
    }

    

    // Tính toán tổng số trang dựa trên số lượng item và số item mỗi trang
    private int calculateTotalPages(int totalItems, int itemsPerPage) {
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }
}
