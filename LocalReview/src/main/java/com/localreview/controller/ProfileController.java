package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.localreview.entity.User;
import com.localreview.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

//    @GetMapping("/profile")
//    public String index(Model model) {
//        List<User> users = userRepository.findAll();
//        model.addAttribute("users", users);
//        return "index";
//    }

    @GetMapping("/profile/{user_id}")
    public String profileById(@PathVariable("user_id") String userId, Model model) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            model.addAttribute("profile", user.get());
        } else {
            model.addAttribute("error", "User not found");
        }
        
        return "profile";
    }
}
