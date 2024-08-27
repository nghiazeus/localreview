package com.localreview.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.localreview.entity.User;
import com.localreview.repository.UserRepository;
import com.localreview.service.UserService;

@Controller
public class HomeController {
	
	
//	@Autowired
//	private UserService userService;
//
//	@GetMapping("/index")
//	public String homePage(Model model, Principal principal) {
//		
//	    if (principal != null) {
//	        // Sử dụng principal.getName() để lấy userId hoặc username
//	        String userId = principal.getName(); // Điều này có thể là userId hoặc username
//	        User user = userService.findByUserId(userId); // Giả sử userId được dùng để tìm User
//	        if (user != null) {
//	            model.addAttribute("user", user);
//	        }
//	    }
//	    return "index"; // tên trang HTML của bạn
//	    
//	}


	 @GetMapping("/index")
	    public String index() {
	        return "index"; // Trả về trang index.html
	    }
	 
	 @GetMapping("/user")
	    public String user() {
	        return "user"; // Trả về trang index.html
	    }

}
