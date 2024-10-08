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
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.CategoriesRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping("/index")
	public String index(Model model) {
		return "index";
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
