package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.localreview.entity.QRCodeScans;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.entityEnum.UserRole;
import com.localreview.repository.StoreRepository;
import com.localreview.service.EmailService;
import com.localreview.service.QRCodeScansService;
import com.localreview.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private QRCodeScansService qrCodeScansService;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Tiêm bean PasswordEncoder

    @PostMapping("/register")
    public String registerUser(@RequestParam("email") String email,
                                @RequestParam("name") String name,
                                @RequestParam("password") String password,
                                @RequestParam("phone_number") String phoneNumber,
                                RedirectAttributes redirectAttributes) {

        try {
           
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password);
            user.setAvatar(null);
            user.setRole(UserRole.user); 
            userService.saveUser(user);

            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình đăng ký. Vui lòng thử lại.");
            return "redirect:/register"; 
        }
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                RedirectAttributes redirectAttributes) {
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password.");
        }
        if (logout != null) {
            redirectAttributes.addFlashAttribute("message", "Logged out successfully.");
        }
        return "login";
    }
    
    @GetMapping("/login/oauth2/success")
    public String handleOAuth2Success(@AuthenticationPrincipal OAuth2User oAuth2User) {
        // Xử lý thông tin người dùng từ Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getName(); // ID Google của người dùng

        // Lưu người dùng vào cơ sở dữ liệu
        userService.findOrCreateUser(email, name, googleId);

        // Chuyển hướng đến trang chính sau khi đăng nhập thành công
        return "redirect:/index";
    }
   
   
}
