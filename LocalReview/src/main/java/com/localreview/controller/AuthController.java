package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.localreview.service.QRCodeScansService;
import com.localreview.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private QRCodeScansService qrCodeScansService;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Tiêm bean PasswordEncoder

    @PostMapping("/register")
    public String registerUserAndStore(@RequestParam("email") String email,
                                        @RequestParam("name") String name,
                                        @RequestParam("password") String password,
                                        @RequestParam("store_name") String storeName,
                                        @RequestParam("address") String address,
                                        @RequestParam("phone_number") String phoneNumber,
                                        RedirectAttributes redirectAttributes) {
        
        
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        user.setRole(UserRole.store_owner);  // Sử dụng enum UserRole
        userService.saveUser(user);

        Store store = new Store();
        store.setStoreName(storeName);
        store.setAddress(address);
        store.setOwnerId(user.getUserId());
        store.setPhoneNumber(phoneNumber);
        storeRepository.save(store);
     // Tạo mã QR cho Store và User đã tạo
        QRCodeScans qrCodeScans = qrCodeScansService.createQRCodeScan(user, store);

        redirectAttributes.addFlashAttribute("message", "Đăng ký thành công với mã QR: " + qrCodeScans.getQrId());
        return "redirect:/index";
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
   
}
