package com.localreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.localreview.entity.Categories;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.CategoriesRepository;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoriesRepository category;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // Lấy thông tin xác thực người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            currentEmail = userDetails.getUsername();
        } else if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            currentEmail = oauthUser.getAttribute("email");
        }

        if (currentEmail != null) {
            User currentUser = userService.findByEmail(currentEmail);
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng.");
            }
        }

        // Thêm danh sách cửa hàng và categories vào model
        List<Store> randomStores = storeService.getRandomStores();
        List<Categories> lists = category.findAll();
        model.addAttribute("stores", randomStores);
        model.addAttribute("categories", lists);
    }
}