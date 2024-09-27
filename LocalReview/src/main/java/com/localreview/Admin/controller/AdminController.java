package com.localreview.Admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        // Trả về tên của view cho trang admin dashboard
        return "admins/index"; // Thay đổi tên view theo cấu trúc thư mục của bạn
    }

    // Có thể thêm các phương thức khác cho các chức năng khác của admin
}
