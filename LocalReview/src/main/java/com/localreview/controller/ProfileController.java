package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
import com.localreview.entity.User;
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


	@GetMapping("/{userId}")
    public String profileById(@PathVariable("userId") String userId, Model model) {
        // Tìm kiếm người dùng theo ID
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            // Thêm thông tin người dùng vào model
            model.addAttribute("profile", user.get());

            // Lấy danh sách cửa hàng của người dùng
            List<Store> stores = storeRepository.findByOwnerId(userId);
            model.addAttribute("stores", stores);

            // Thêm breadcrumb vào model
            List<Breadcrumb> breadcrumbs = new ArrayList<>();
            breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
            breadcrumbs.add(new Breadcrumb("Profile", "/profile/" + userId));

            model.addAttribute("breadcrumbs", breadcrumbs);

            return "profile"; // Trang profile.html sẽ được sử dụng để hiển thị thông tin cá nhân
        } else {
            model.addAttribute("error", "User not found");
            return "error"; // Chuyển hướng đến trang lỗi hoặc thông báo lỗi
        }
    }

	@PostMapping("/update")
	public String updateProfile(@RequestParam("userId") String userId, @ModelAttribute("profile") User updatedUser,
			Model model) {
		Optional<User> existingUser = userRepository.findById(userId);

		if (existingUser.isPresent()) {
			User user = existingUser.get();
			user.setName(updatedUser.getName());
			user.setEmail(updatedUser.getEmail());
			user.setPhoneNumber(updatedUser.getPhoneNumber());

			// Xử lý googleId
			if (updatedUser.getGoogleId() != null && !updatedUser.getGoogleId().isEmpty()) {
				user.setGoogleId(updatedUser.getGoogleId());
			} else {
				user.setGoogleId(null); // Hoặc không thay đổi nếu không cần thiết
			}

			userRepository.save(user); // Lưu thông tin người dùng đã cập nhật vào cơ sở dữ liệu
			model.addAttribute("successMessage", "Thông tin cá nhân đã được cập nhật thành công.");
		} else {
			model.addAttribute("errorMessage", "Không tìm thấy người dùng.");
		}

		return "redirect:/profile/" + userId; // Chuyển hướng lại trang profile
	}

	@PostMapping("/updatestore")
	public String updateStore(Store store, Model model) {
		Store existingStore = storeRepository.findById(store.getStoreId()).orElse(null);
		if (existingStore != null) {
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


}
