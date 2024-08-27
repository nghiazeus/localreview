package com.localreview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	 @GetMapping("/index")
	    public String index() {
	        return "index"; // Trả về trang index.html
	    }
	 
	 @GetMapping("/user")
	    public String user() {
	        return "user"; // Trả về trang index.html
	    }
}
