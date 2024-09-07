package com.localreview.controller;

import com.localreview.entity.Breadcrumb;
import com.localreview.entity.Categories;
import com.localreview.entity.QRCodeScans;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.entityEnum.UserRole;
import com.localreview.repository.CategoriesRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;
import com.localreview.service.EmailService;
import com.localreview.service.QRCodeScansService;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

import java.util.Arrays;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StoreController {

	@Autowired
	private StoreService storeService;

	@Autowired
	private StoreRepository storere;

	@Autowired
	private UserRepository userrepon;

	@Autowired
	private EmailService emailService;

	@Autowired
	private QRCodeScansService qrCodeScansService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoriesRepository category;

	@GetMapping("/register-store")
	public String Registersore(Model model) {
		List<Categories> list = category.findAll();
		model.addAttribute("categories", list);
		return "register-store";
	}

	@GetMapping("/categorie")
	public String showStoreRegistrationForm(Model model) {
		List<Categories> list = category.findAll();
		model.addAttribute("categories", list);
		return "register-store";
	}
	


	@PostMapping("/register-store")
	public String registerStore(@RequestParam("store_name") String storeName,
			@RequestParam("store_categories") Categories storeCategories, @RequestParam("address_city") String addressCity,
			@RequestParam("address_district") String addressDistrict,
			@RequestParam("address_commune") String addressCommune,
			@RequestParam("address_street") String addressStreet, @RequestParam("phone_number") String phoneNumber,
			RedirectAttributes redirectAttributes) {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String currentID;

			// Kiểm tra nếu đăng nhập bằng Google (OAuth2AuthenticationToken)
			if (authentication instanceof OAuth2AuthenticationToken) {
				OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
				Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
				currentID = (String) attributes.get("email"); // Lấy email từ OAuth2 user
			} else {
				currentID = authentication.getName(); // Sử dụng cách thông thường cho đăng nhập bình thường
			}

			// Kiểm tra sự tồn tại của người dùng
			User currentUser = userService.findByEmail(currentID);
			if (currentUser == null) {
				redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại. Vui lòng đăng nhập lại.");
				return "redirect:/register-store";
			}
			User role = userrepon.findByEmail(currentID);
			if (role != null) {
				role.setRole(UserRole.store_owner);
			}

			Store store = new Store();
			store.setStoreName(storeName);
			store.setStoreCategories(storeCategories);
			store.setAddressCity(addressCity);
			store.setAddressDistrict(addressDistrict);
			store.setAddressCommune(addressCommune);
			store.setAddressStreet(addressStreet);
			store.setPhoneNumber(phoneNumber);
			store.setOwnerId(currentUser.getUserId());

			storeService.saveStore(store); // Lưu trữ đối tượng store vào cơ sở dữ liệu

			QRCodeScans qrCodeScans = qrCodeScansService.createQRCodeScan(currentUser, store);

			// Tạo nội dung email
			String subject = "Đăng ký thành công hệ thống đánh giá System Review!";

			// Gửi email
			emailService.sendRegistrationEmail(currentUser.getEmail(), subject, currentUser.getName(), storeName,
					qrCodeScans.getQrCodeUrl());

			// Thêm thông báo và chuyển hướng
			redirectAttributes.addFlashAttribute("message", "Đăng ký thành công với mã QR: " + qrCodeScans.getQrId());

			redirectAttributes.addFlashAttribute("success", "Đăng ký cửa hàng thành công!");
			return "redirect:/profile/" + currentUser.getUserId();

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error",
					"Đã xảy ra lỗi trong quá trình đăng ký cửa hàng. Vui lòng thử lại.");
			return "redirect:/register-store";
		}
	}
//	--Store detail

	@GetMapping("/store/detail/{id}")
	public String showStoreDetail(@PathVariable("id") String id, Model model) {
		try {
			Store store = storeService.findStoreById(id);
			if (store != null) {
				model.addAttribute("store", store);

				// Thêm thông tin breadcrumb
				List<Breadcrumb> breadcrumbs = Arrays.asList(new Breadcrumb("Trang chủ", "/index"),
						new Breadcrumb("Cửa hàng", "/stores"),
						new Breadcrumb(store.getStoreName(), "/store/detail/" + store.getStoreId()));
				model.addAttribute("breadcrumbs", breadcrumbs);

				return "store-detail"; // Tên của view HTML
			} else {
				model.addAttribute("error", "Cửa hàng không tồn tại.");
				return "error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Đã xảy ra lỗi khi lấy thông tin cửa hàng.");
			return "error";
		}
	}

}
