package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.localreview.entity.Breadcrumb;
import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreMenuRepository storeMenuRepository;

    @GetMapping("/user")
    public String profileByLoggedInUser(Model model, Principal principal) {
        String email = null;
        String googleId = null;

        // Kiểm tra xem principal có phải là OAuth2AuthenticationToken không
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;
            email = authToken.getPrincipal().getAttribute("email");
            googleId = authToken.getPrincipal().getAttribute("sub");

            // Log thông tin để kiểm tra
            System.out.println("Email: " + email);
            System.out.println("Google ID: " + googleId);
        } else {
            // Nếu principal không phải OAuth2, có thể là người dùng đăng nhập bằng email
            email = principal.getName(); // Giả định rằng tên chính là email
        }

        // Tìm thông tin người dùng dựa trên email
        User user = userRepository.findByEmail(email);

        // Nếu không tìm thấy người dùng, tìm bằng Google ID
        if (user == null && googleId != null) {
            user = userRepository.findByGoogleId(googleId);
        }

        // Nếu vẫn không tìm thấy người dùng
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy người dùng với email: " + email + " hoặc Google ID: " + googleId);
            return "error"; // Trả về trang lỗi
        }

        // Nếu tìm thấy người dùng, thêm thông tin vào model
        model.addAttribute("profile", user);

        // Lấy danh sách cửa hàng của người dùng
        List<Store> stores = storeRepository.findByOwnerId(user.getUserId());
        model.addAttribute("stores", stores);

        // Thêm breadcrumb vào model
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
        breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile"));
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "profile"; // Hiển thị trang profile.html với thông tin cá nhân
    }






    @PostMapping("/updatethongtincanhan")
    public String updateProfile(
        @ModelAttribute("profile") User updatedUser, 
        Principal principal, 
        RedirectAttributes redirectAttributes) {

        String email = null;
        String googleId = null;

        // Kiểm tra xem principal có phải là OAuth2AuthenticationToken không
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;
            email = authToken.getPrincipal().getAttribute("email");
            googleId = authToken.getPrincipal().getAttribute("sub");
        } else {
            // Nếu principal không phải OAuth2, lấy email từ tên
            email = principal.getName(); // Giả định rằng tên là email
        }

        // Tìm người dùng dựa trên email
        User existingUser = userRepository.findByEmail(email);

        // Nếu không tìm thấy người dùng, tìm bằng Google ID nếu có
        if (existingUser == null && googleId != null) {
            existingUser = userRepository.findByGoogleId(googleId);
        }

        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

            // Cập nhật Google ID nếu có
            if (updatedUser.getGoogleId() != null && !updatedUser.getGoogleId().isEmpty()) {
                existingUser.setGoogleId(updatedUser.getGoogleId());
            } else {
                existingUser.setGoogleId(null);
            }

            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("success", "Thông tin cá nhân đã được cập nhật thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
        }

        return "redirect:/profile/user"; // Trở về trang profile mà không cần userId
    }


    

    @PostMapping("/updatestore")
    public String updateStore(@ModelAttribute("store") Store store, Model model) {
        Optional<Store> existingStoreOptional = storeRepository.findById(store.getStoreId());

        if (existingStoreOptional.isPresent()) {
            Store existingStore = existingStoreOptional.get();
            existingStore.setStoreName(store.getStoreName());
            existingStore.setAddressCity(store.getAddressCity());
            existingStore.setAddressDistrict(store.getAddressDistrict());
            existingStore.setAddressCommune(store.getAddressCommune());
            existingStore.setAddressStreet(store.getAddressStreet());
            existingStore.setPhoneNumber(store.getPhoneNumber());

            storeRepository.save(existingStore);

            model.addAttribute("store", existingStore);
            model.addAttribute("success", "Store updated successfully");

            return "redirect:/profile/" + existingStore.getOwnerId();
        } else {
            model.addAttribute("error", "Store not found");
            return "error";
        }
    }

    @PostMapping("/updatestoremenu")
    public String updateStoreMenu(@RequestParam("menuId") String menuId, 
                                  @RequestParam("storeId") String storeId, 
                                  @RequestParam("foodFirst") String foodFirst, 
                                  @RequestParam("foodMain") String foodMain, 
                                  @RequestParam("foodDessert") String foodDessert, 
                                  RedirectAttributes redirectAttributes) {
        // Lấy đối tượng Store từ cơ sở dữ liệu
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (!storeOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Store not found");
            return "redirect:/error"; // Chuyển hướng đến trang lỗi
        }

        Store store = storeOptional.get();

        // Lấy đối tượng StoreMenu từ cơ sở dữ liệu
        Optional<StoreMenu> menuOptional = storeMenuRepository.findById(menuId);
        if (!menuOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Menu not found");
            return "redirect:/error"; // Chuyển hướng đến trang lỗi
        }

        StoreMenu storeMenu = menuOptional.get();
        storeMenu.setStore(store); // Gán đối tượng Store vào StoreMenu
        storeMenu.setFoodFirst(foodFirst);
        storeMenu.setFoodMain(foodMain);
        storeMenu.setFoodDessert(foodDessert);

        storeMenuRepository.save(storeMenu);

        redirectAttributes.addFlashAttribute("successMessage", "Menu updated successfully");
        return "redirect:/profile/" + store.getOwnerId(); // Chuyển hướng đến trang profile của người sở hữu cửa hàng
    }

}
