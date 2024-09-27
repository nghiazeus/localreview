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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

	@GetMapping("/store")
	public String store(Model model) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Cửa hàng", "/stores"));
		model.addAttribute("breadcrumbs", breadcrumbs);
		return "stores/stores";
	}

//	Search........
	@GetMapping("/store/search")
	public String searchStores(@RequestParam("query") String query, Model model) {
		List<Store> searchResults = storeService.searchStores(query);
		model.addAttribute("stores", searchResults);

		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Cửa hàng", "/store"));
		breadcrumbs.add(new Breadcrumb("Tìm kiếm", "/store/search?query=" + query));
		breadcrumbs.add(new Breadcrumb("" + query, "/store/search?query= " + query));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/stores";
	}

//	Đăng ký của hàng..........

//	Localhost đăng ký
	@GetMapping("/register-store")
	public String Registersore(Model model) {
		List<Categories> list = category.findAll();
		model.addAttribute("categories", list);
		return "stores/register-store";
	}

//	Đăng ký cửa hàng

	@PostMapping("/register-store")
	public String registerStore(@RequestParam("store_name") String storeName,
			@RequestParam("store_categories") Categories storeCategories,
			@RequestParam("address_city") String addressCity, @RequestParam("address_district") String addressDistrict,
			@RequestParam("address_commune") String addressCommune,
			@RequestParam("address_street") String addressStreet, @RequestParam("phone_number") String phoneNumber,
			@RequestParam("store_images") MultipartFile[] storeImages, // Nhiều ảnh cửa hàng
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

			// Tạo đối tượng Store và lưu thông tin
			Store store = new Store();
			store.setStoreName(storeName);
			store.setStoreCategories(storeCategories);
			store.setAddressCity(addressCity);
			store.setAddressDistrict(addressDistrict);
			store.setAddressCommune(addressCommune);
			store.setAddressStreet(addressStreet);
			store.setPhoneNumber(phoneNumber);
			store.setOwnerId(currentUser.getUserId());

			// Lưu đối tượng store vào cơ sở dữ liệu trước khi lưu ảnh
			Store savedStore = storeService.saveStore(store);

			// Xử lý và lưu nhiều ảnh
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
			return "redirect:/profile/" + currentUser.getUserId();

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
    public String showStoreDetail(@PathVariable("storeId") String storeId, Model model) {
        try {
            // Lấy thông tin cửa hàng theo storeId
            Store store = storeService.findStoreById(storeId);
            if (store != null) {
                // Thêm thông tin cửa hàng vào model
                model.addAttribute("store", store);

                // Lấy danh sách menu, đồ ăn, đồ uống và ảnh của cửa hàng
                List<StoreMenu> storeMenuList = storeMenuService.findByStore_StoreId(storeId);
                List<StoreFood> storeFoodList = storeFoodService.findByStore_StoreId(storeId);
                List<StoreDrink> storeDrinkList = storeDrinkService.findByStore_StoreId(storeId);
                List<Photo> storePhotos = photoService.getPhotosByStoreId(storeId);
                List<Review> reviews = reviewService.getReviewsByStore(storeId);

                // Format giá cho danh sách đồ ăn
                for (StoreFood food : storeFoodList) {
                    food.getFormattedPrice();
                }

                // Tạo map chứa danh sách ảnh theo từng review
                Map<String, List<String>> reviewPhotosMap = reviews.stream()
                    .collect(Collectors.toMap(Review::getReviewId, 
                    review -> photoService.getPhotosByReviewId(review.getReviewId())
                                           .stream().map(Photo::getPhotoUrl)
                                           .collect(Collectors.toList())));

                // Thêm danh sách menu, đồ ăn, đồ uống, và ảnh vào model
                model.addAttribute("menulist", storeMenuList);
                model.addAttribute("foodlist", storeFoodList);
                model.addAttribute("drinklist", storeDrinkList);
                model.addAttribute("storePhotos", storePhotos);
                model.addAttribute("reviews", reviews);
                model.addAttribute("reviewPhotosMap", reviewPhotosMap);

                // Tạo breadcrumb cho đường dẫn hiện tại
                List<Breadcrumb> breadcrumbs = new ArrayList<>();
                breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
                breadcrumbs.add(new Breadcrumb("Cửa hàng", "/store"));
                breadcrumbs.add(new Breadcrumb(store.getStoreName(), "/store/detail/" + store.getStoreId()));
                model.addAttribute("breadcrumbs", breadcrumbs);

                // Trả về trang chi tiết cửa hàng
                return "stores/store-detail";
            } else {
                // Nếu cửa hàng không tồn tại, trả về trang lỗi
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
