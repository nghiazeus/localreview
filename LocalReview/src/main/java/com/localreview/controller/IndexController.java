package com.localreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

	@Autowired
    private  StoreService storesv;
    
    @Autowired
    private  UserService usersv;



    @GetMapping("/index")
    public String store(Model model) {
        // Lấy thông tin xác thực hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            currentEmail = userDetails.getUsername(); // Lấy email của người dùng hiện tại
        }

        // Lấy thông tin người dùng từ email
        User currentUser = usersv.findByEmail(currentEmail);
        if (currentUser != null) {
            model.addAttribute("name", currentUser.getName()); // Thêm tên vào model
        }

        // Lấy danh sách các cửa hàng
        List<Store> list = storesv.getAllStores();
        model.addAttribute("stores", list); // Thêm danh sách cửa hàng vào model

        return "index"; // Trả về tên của trang Thymeleaf
    }
    
    @GetMapping("/storedetail")
    public String Storedetail() {
        return "detailstore";
    }
    
    @GetMapping("/user")
    public String user() {
        return "index"; // Trả về trang index.html
    }
    
}

