package com.localreview.controller;

import com.localreview.entity.Breadcrumb;
import com.localreview.entity.Categories;
import com.localreview.entity.Photo;
import com.localreview.entity.QRCodeScans;
import com.localreview.entity.Review;
import com.localreview.entity.Store;
import com.localreview.entity.StoreDrink;
import com.localreview.entity.StoreFood;
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;
import com.localreview.entityEnum.UserRole;
import com.localreview.repository.CategoriesRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;
import com.localreview.service.CategoriesService;
import com.localreview.service.EmailService;
import com.localreview.service.PhotoService;
import com.localreview.service.QRCodeScansService;
import com.localreview.service.ReviewService;
import com.localreview.service.StoreDrinkService;
import com.localreview.service.StoreFoodService;
import com.localreview.service.StoreMenuService;
import com.localreview.service.StoreService;
import com.localreview.service.UserFavoritesService;
import com.localreview.service.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
	private CategoriesService categoriesService;

	@Autowired
	private CategoriesRepository category;

	@Autowired
	private StoreMenuService storeMenuService;

	@Autowired
	private StoreFoodService storeFoodService;

	@Autowired
	private StoreDrinkService storeDrinkService;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private ReviewService reviewService;
	
    @Autowired
    private UserFavoritesService userFavoritesService;

	@GetMapping("/store")
	public String store(Model model) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Cửa hàng", "/stores"));
		model.addAttribute("breadcrumbs", breadcrumbs);
		return "stores/stores";
	}
	

	
	@GetMapping("/register-store")
	public String Registersore(Model model) {
		List<Categories> list = category.findAll();
		model.addAttribute("categories", list);
		return "stores/register-store";
	}


	@PostMapping("/register-store")
	public String registerStore(@RequestParam("store_name") String storeName,
			@RequestParam("store_categories") Categories storeCategories,
			@RequestParam("address_city") String addressCity, @RequestParam("address_district") String addressDistrict,
			@RequestParam("address_commune") String addressCommune,
			@RequestParam("address_street") String addressStreet, @RequestParam("phone_number") String phoneNumber,
			@RequestParam("store_images") MultipartFile[] storeImages,
			RedirectAttributes redirectAttributes) {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String currentID;
	
			if (authentication instanceof OAuth2AuthenticationToken) {
				OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
				Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
				currentID = (String) attributes.get("email"); 
			} else {
				currentID = authentication.getName(); 
			}

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

			Store savedStore = storeService.saveStore(store);

			if (storeImages != null && storeImages.length > 0) {
				for (MultipartFile image : storeImages) {
					if (!image.isEmpty()) {
						Path tempFilePath = null;

						try {
							// Lưu file tạm thời
							tempFilePath = saveFile(image);

							// Upload lên Imgur và lấy URL
							String imageUrl = storeService.uploadImageToImgur(tempFilePath.toString());

							// Tạo đối tượng Photo và lưu vào cơ sở dữ liệu
							Photo photo = new Photo();
							photo.setPhotoUrl(imageUrl);
							photo.setStoreId(savedStore.getStoreId()); // Gán storeId cho ảnh
							photo.setPhotoType("store"); // Gán loại ảnh là review

							storeService.savePhoto(photo);
						} catch (IOException e) {
							e.printStackTrace();
							return "redirect:/register-store?error=upload_failed";
						} finally {
							// Đảm bảo xóa file tạm thời
							if (tempFilePath != null && Files.exists(tempFilePath)) {
								try {
									Files.delete(tempFilePath);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			QRCodeScans qrCodeScans = qrCodeScansService.createQRCodeScan(currentUser, savedStore);

			// Tạo nội dung email
			String subject = "Đăng ký thành công hệ thống đánh giá System Review!";

			// Gửi email
			emailService.sendRegistrationEmail(currentUser.getEmail(), subject, currentUser.getName(), storeName,
					qrCodeScans.getQrCodeUrl());

			// Thêm thông báo và chuyển hướng
			redirectAttributes.addFlashAttribute("message", "Đăng ký thành công với mã QR: " + qrCodeScans.getQrId());
			redirectAttributes.addFlashAttribute("success", "Đăng ký cửa hàng thành công!");
			return "redirect:/profile/user";

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error",
					"Đã xảy ra lỗi trong quá trình đăng ký cửa hàng. Vui lòng thử lại.");
			return "redirect:/register-store";
		}
	}

//	--Store detail

	private Path saveFile(MultipartFile file) throws IOException {
		Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
		file.transferTo(tempFile.toFile());
		return tempFile;
	}

//	----------------------------------
	@GetMapping("/store/detail/{storeId}")
	public String showStoreDetail(@PathVariable("storeId") String storeId, 
	        @AuthenticationPrincipal UserDetails userDetails,
	        Model model, HttpSession session) {
	    try {
	        Store store = storeService.findStoreById(storeId);
	        if (store != null) {
	            Set<String> viewedStores = (Set<String>) session.getAttribute("viewedStores");
	            if (viewedStores == null) {
	                viewedStores = new HashSet<>();
	            }

	            if (!viewedStores.contains(storeId)) {
	                storeService.incrementViewCount(storeId);
	                viewedStores.add(storeId);
	                session.setAttribute("viewedStores", viewedStores);
	            }

	            model.addAttribute("store", store);

	            List<StoreMenu> storeMenuList = storeMenuService.findByStore_StoreId(storeId);
	            List<StoreFood> storeFoodList = storeFoodService.findByStore_StoreId(storeId);
	            List<StoreDrink> storeDrinkList = storeDrinkService.findByStore_StoreId(storeId);
	            List<Photo> storePhotos = photoService.getPhotosByStoreId(storeId);
	            Double averageRating = storeService.getAverageRating(storeId);

	            User currentUser = userDetails != null ? userService.findByEmail(userDetails.getUsername()) : null;

	            boolean isFavorited = currentUser != null && userFavoritesService.existsByUserAndStore(currentUser, store);
	            store.setFavorited(isFavorited);

	            String formattedAverageRating = String.format("%.1f", averageRating);
	            int fullStars = averageRating.intValue();
	            boolean hasHalfStar = (averageRating - fullStars >= 0.5);
	            int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

	            for (StoreFood food : storeFoodList) {
	                food.getFormattedPrice();
	            }

	            model.addAttribute("menulist", storeMenuList);
	            model.addAttribute("foodlist", storeFoodList);
	            model.addAttribute("drinklist", storeDrinkList);
	            model.addAttribute("storePhotos", storePhotos);
	            model.addAttribute("fullStars", fullStars);
	            model.addAttribute("hasHalfStar", hasHalfStar);
	            model.addAttribute("emptyStars", emptyStars);
	            model.addAttribute("averageRating", formattedAverageRating);

	            List<Breadcrumb> breadcrumbs = new ArrayList<>();
	            breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
	            breadcrumbs.add(new Breadcrumb("Cửa hàng", "/store"));
	            breadcrumbs.add(new Breadcrumb(store.getStoreName(), "/store/detail/" + store.getStoreId()));
	            model.addAttribute("breadcrumbs", breadcrumbs);

	            return "stores/store-detail";
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


//	-----------------------------------------------

	@GetMapping("/store/category")
	public String getStoresByCategory(@RequestParam("categoryId") String categoryId, Model model) {
		List<Store> stores = storeService.getStoresByCategoryId(categoryId);
		model.addAttribute("stores", stores);
		model.addAttribute("categoryId", categoryId);

		// Lấy danh mục từ ID
		Optional<Categories> categoryOpt = categoriesService.getCategoryById(categoryId);
		String categoryName = categoryOpt.map(Categories::getCategoriesName).orElse("Danh mục không xác định");

		// Thêm breadcrumb với tên danh mục
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Cửa hàng", "/store"));
		breadcrumbs.add(new Breadcrumb("Danh mục", "/categories"));
		breadcrumbs.add(new Breadcrumb(categoryName, "/stores/category?categoryId=" + categoryId));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/stores";
	}

}
