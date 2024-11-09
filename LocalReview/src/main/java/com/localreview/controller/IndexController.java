package com.localreview.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.localreview.entity.Breadcrumb;
import com.localreview.entity.Categories;
import com.localreview.entity.CustomUserDetails;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.CategoriesRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SessionAttributes("userId")
public class IndexController {

	@GetMapping("/index")
	public String index(Model model) {
	    // Lấy thông tin người dùng từ Principal (authentication)
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    Object principal = authentication.getPrincipal();

	    String userId = null;

	    if (principal instanceof CustomUserDetails) {
	        // Nếu là CustomUserDetails, lấy userId từ CustomUserDetails
	        CustomUserDetails userDetails = (CustomUserDetails) principal;
	        userId = userDetails.getUserId();
	        System.out.println("User ID retrieved from CustomUserDetails in index: " + userId);
	    } else if (principal instanceof DefaultOAuth2User) {
	        // Nếu là OAuth2User (Google OAuth2), lấy userId từ attributes của DefaultOAuth2User
	        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) principal;
	        
	        // Lấy userId từ attributes (đã được thêm vào trong CustomOAuth2UserService)
	        userId = oAuth2User.getAttribute("userId");
	        System.out.println("User ID retrieved from Google OAuth2User in index: " + userId);
	    }

	    if (userId != null) {
	        model.addAttribute("userId", userId);
	    } else {
	        System.out.println("User details are not available. Authentication may not be valid.");
	    }

	    return "index"; // Trả về tên của file index.html
	}



	@GetMapping("/qrcode")
	public String home(Model model) {
		return "qrcode";
	}

	@GetMapping("/categories")
	public String categories(Model model) {

		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Loại cửa hàng", "/categories"));
		
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "categories";
	}

	@GetMapping("/test")
	public String test(Model model) {
		return "test";
	}

}
