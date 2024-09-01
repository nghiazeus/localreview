package com.localreview.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Autowired
    private  StoreService storesv;
    
    @Autowired
    private  UserService usersv;
    
	@Autowired
	private CategoriesRepository category;
	
	@Autowired UserRepository ustory;



	@GetMapping("/index")
	public String store(Model model) {
	    // Lấy thông tin xác thực hiện tại
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentEmail = null;

	    // Kiểm tra nếu authentication là hợp lệ và principal là UserDetails
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            currentEmail = userDetails.getUsername(); // Lấy email của người dùng hiện tại
        } else if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            currentEmail = oauthUser.getAttribute("email"); // Lấy email từ OAuth2
        }

        // Lấy thông tin người dùng từ email nếu email không phải là null
        if (currentEmail != null) {
            User currentUser = usersv.findByEmail(currentEmail);
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser); // Thêm người dùng vào model
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng.");
            }
        }


	    // Lấy danh sách các cửa hàng
	    List<Store> list = storesv.getAllStores();
	    List<Categories> lists = category.findAll();
	    model.addAttribute("stores", list);
	    model.addAttribute("categories", lists);

	    return "index"; // Trả về tên của trang Thymeleaf
	}

	
	
    @GetMapping("/storedetail")
    public String Storedetail() {
        return "detailstore";
    }
    
    
}

