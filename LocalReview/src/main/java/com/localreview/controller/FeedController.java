package com.localreview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedController {
	
	@GetMapping("/feed")
	public String feed(Model model) {
		return "feed";
	}

}
