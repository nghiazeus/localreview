package com.localreview.Admin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.localreview.entity.Blacklist;
import com.localreview.service.BlacklistService;

@Controller
@RequestMapping("/admin/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @Autowired
    public BlacklistController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    // Hiển thị tất cả blacklist entries
    @GetMapping
    public String getAllBlacklists(Model model) {
        List<Blacklist> blacklists = blacklistService.getAllBlacklists();
        
        model.addAttribute("blacklistItems", blacklists); // Sử dụng "blacklistItems" để đồng bộ với template
        model.addAttribute("currentPage", 1); // Giả định trang hiện tại là 1
        model.addAttribute("totalPage", calculateTotalPages(blacklists.size(), 10)); // Thay 10 bằng số item mỗi trang
        return "admins/blacklist"; // Trả về tên view (blacklist.html)
    }

    // Hiển thị thông tin blacklist theo ID
    @GetMapping("/{id}")
    public String getBlacklistById(@PathVariable String id, Model model) {
        Optional<Blacklist> blacklist = blacklistService.getBlacklistById(id);
        if (blacklist.isPresent()) {
            model.addAttribute("blacklist", blacklist.get());
            return "blacklistDetail"; // Trả về tên view cho chi tiết blacklist
        } else {
            return "notFound"; // Trả về view không tìm thấy nếu không tìm thấy
        }
    }

    // Thêm người dùng vào blacklist
    @PostMapping
    public String addUserToBlacklist(@ModelAttribute Blacklist blacklist) {
        blacklistService.addUserToBlacklist(blacklist);
        return "redirect:/admin/blacklist"; // Chuyển hướng về trang danh sách blacklist
    }

    // Xóa blacklist entry theo ID
    @PostMapping("/delete/{id}")
    public String deleteBlacklistById(@PathVariable String id) {
        blacklistService.deleteBlacklistById(id);
        return "redirect:/admin/blacklist"; // Chuyển hướng về trang danh sách blacklist
    }

    // Kiểm tra xem người dùng có trong blacklist không
    @GetMapping("/check/{userId}")
    public @ResponseBody boolean isUserBlacklisted(@PathVariable String userId) {
        return blacklistService.isUserBlacklisted(userId);
    }

    // Tính toán tổng số trang dựa trên số lượng item và số item mỗi trang
    private int calculateTotalPages(int totalItems, int itemsPerPage) {
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }
}
