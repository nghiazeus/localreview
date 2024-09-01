package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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

    @GetMapping("/profile")
    public String profile(Model model) {
        // Lấy thông tin người dùng hiện tại từ session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = null;

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                currentUserEmail = userDetails.getUsername(); // Lấy email của người dùng hiện tại
            } else if (principal instanceof DefaultOAuth2User) {
                DefaultOAuth2User oAuth2User = (DefaultOAuth2User) principal;
                // Lấy email từ DefaultOAuth2User
                currentUserEmail = (String) oAuth2User.getAttributes().get("email");
            }
        }

        // Lấy thông tin người dùng từ email
        if (currentUserEmail != null) {
            User currentUser = userRepository.findByEmail(currentUserEmail);
            if (currentUser != null) {
                model.addAttribute("profile", currentUser); // Thêm người dùng vào model
            }
        }

        return "profile"; // Trả về trang profile
    }

}
