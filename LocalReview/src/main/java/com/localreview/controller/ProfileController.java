package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/{userId}")
    public String profileById(@PathVariable("userId") String userId, Model model) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("profile", user);

            // Lấy danh sách cửa hàng của người dùng
            List<Store> stores = storeRepository.findByOwnerId(userId);
            model.addAttribute("stores", stores);

            // Thêm breadcrumb vào model
            List<Breadcrumb> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
            breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + userId));
            model.addAttribute("breadcrumbs", breadcrumbs);

            return "profile"; // Trang profile.html sẽ được sử dụng để hiển thị thông tin cá nhân
        } else {
            model.addAttribute("error", "User not found");
            return "error"; // Chuyển hướng đến trang lỗi hoặc thông báo lỗi
        }
    }


    @PostMapping("/updatethongtincanhan")
    public String updateProfile(
        @RequestParam("userId") String userId, 
        @ModelAttribute("profile") User updatedUser, 
        RedirectAttributes redirectAttributes) {

        Optional<User> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());

            if (updatedUser.getGoogleId() != null && !updatedUser.getGoogleId().isEmpty()) {
                existingUser.setGoogleId(updatedUser.getGoogleId());
            } else {
                existingUser.setGoogleId(null);
            }

            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng.");
        }

        return "redirect:/profile/" + userId;
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
