package com.localreview.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.localreview.entity.Categories;
import com.localreview.entity.Photo;
import com.localreview.entity.Store;
import com.localreview.entity.StoreDrink;
import com.localreview.entity.StoreFood;
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;
import com.localreview.entity.Breadcrumb; // Giả sử bạn có một lớp Breadcrumb
import com.localreview.service.CategoriesService;
import com.localreview.service.PhotoService;
import com.localreview.service.StoreDrinkService;
import com.localreview.service.StoreFoodService;
import com.localreview.service.StoreMenuService;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;
import com.localreview.repository.CategoriesRepository;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;

@Controller
@RequestMapping("/stores")
public class StoreOfUserController {

//	Repository---------------------
	@Autowired
	private CategoriesRepository categoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StoreMenuRepository menuRepository;

//  Service-----------------------
	@Autowired
	private UserService userService;
	@Autowired
	private StoreService storeService;

	@Autowired
	private StoreMenuService storeMenuService;

	@Autowired
	private StoreFoodService storeFoodService;

	@Autowired
	private StoreDrinkService storeDrinkService;
	
	@Autowired
	private CategoriesService categoryService;
	
	@Autowired
	private PhotoService photoService;

	@GetMapping("/my-stores") // Đường dẫn không cần userId
	public String getUserStores(@RequestParam(required = false) String storeId, Model model) {
	    // Lấy thông tin người dùng đang đăng nhập
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = ((UserDetails) authentication.getPrincipal()).getUsername(); // Hoặc cách lấy email phù hợp với lớp User của bạn

	    // Tìm người dùng theo email
	    User currentUser = userRepository.findByEmail(email);
	    if (currentUser == null) {
	        model.addAttribute("error", "Không tìm thấy người dùng");
	        return "error";
	    }

	    List<Store> stores = storeService.getStoresByOwnerId(currentUser.getUserId());
	    // List<Store> stores = storeService.getStoresByOwnerId(userId);
	    List<Photo> storePhotos = photoService.getPhotosByStoreId(storeId);
	    
	    model.addAttribute("stores", stores);
	    model.addAttribute("storePhotos", storePhotos);

	    List<Breadcrumb> breadcrumbs = new ArrayList<>();
	    breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
	    breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + currentUser.getUserId()));
	    breadcrumbs.add(new Breadcrumb("Cửa hàng của tôi", "/stores")); // Cập nhật đường dẫn breadcrumb
	    model.addAttribute("breadcrumbs", breadcrumbs);

	    return "stores/user_stores";
	}

	@GetMapping("/edit/{storeId}")
	public String showEditForm(@PathVariable("storeId") String storeId, Model model) {
	    Store store = storeService.getStoreById(storeId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid store Id:" + storeId));
	    model.addAttribute("store", store);
	    
	    // Lấy danh sách danh mục để hiển thị trong dropdown
	    List<Categories> categories = categoryRepository.findAll(); // Hoặc phương thức tương tự
	    model.addAttribute("categories", categories);
	    
	    return "stores/edit_user_store";
	}


	@PostMapping("/update-store")
	public String updateStore(@RequestParam("storeId") String storeId,
	                           @RequestParam("storeName") String storeName,
	                           @RequestParam("storeCategories") String storeCategoriesId,
	                           @RequestParam("addressStreet") String addressStreet,
	                           @RequestParam("addressCommune") String addressCommune,
	                           @RequestParam("addressDistrict") String addressDistrict,
	                           @RequestParam("addressCity") String addressCity,
	                           @RequestParam("phoneNumber") String phoneNumber,
	                           @RequestParam(value = "photo", required = false) MultipartFile photo,
	                           RedirectAttributes redirectAttributes) throws IOException {

	    try {
	        // Lấy cửa hàng theo ID
	        Store store = storeService.getStoreById(storeId)
	                .orElseThrow(() -> new IllegalArgumentException("Invalid store Id:" + storeId));

	        // Cập nhật thông tin cửa hàng
	        store.setStoreName(storeName);

	        // Lấy danh mục từ ID
	        Categories category = categoryService.getCategoryById(storeCategoriesId)
	                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + storeCategoriesId));
	        store.setStoreCategories(category);

	        store.setAddressStreet(addressStreet);
	        store.setAddressCommune(addressCommune);
	        store.setAddressDistrict(addressDistrict);
	        store.setAddressCity(addressCity);
	        store.setPhoneNumber(phoneNumber);

	        // Xử lý ảnh
	        if (photo != null && !photo.isEmpty()) {
	            Path tempFilePath = null;
	            try {
	                tempFilePath = saveFile(photo);
	                String imageUrl = storeService.uploadImageToImgur(tempFilePath.toString());
	                // Cập nhật ảnh mới cho cửa hàng
	                store.getPhotos().clear(); // Xóa ảnh cũ nếu cần
	                Photo newPhoto = new Photo();
	                newPhoto.setPhotoUrl(imageUrl);
	                newPhoto.setStoreId(storeId);
	                storeService.savePhoto(newPhoto);
	            } finally {
	                if (tempFilePath != null && Files.exists(tempFilePath)) {
	                    try {
	                        Files.delete(tempFilePath);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }

	        // Lưu cửa hàng
	        storeService.saveStore(store);

	        // Lấy userId từ cửa hàng
	        String userId = store.getOwnerId();

	        // Redirect đến trang của người dùng
	        redirectAttributes.addFlashAttribute("success", "Cập nhật cửa hàng thành công!");
	        return "redirect:/stores/" + userId;

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi cập nhật cửa hàng.");
	        return "redirect:/stores/edit/" + storeId;
	    }
	}
	
	
//	Delete Store
	@PostMapping("/delete")
    public String deleteStore(@RequestParam("storeId") String storeId) {
        storeService.deleteStore(storeId);
        return "redirect:/stores"; 
    }



	private Path saveFile(MultipartFile file) throws IOException {
		Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
		file.transferTo(tempFile.toFile());
		return tempFile;
	}


//    Menu-----------------------
	// Hiển thị danh sách menu của cửa hàng
	@GetMapping("/menu")
	public String getStoreMenu(@RequestParam("storeId") String storeId, Model model) {
		// Tìm cửa hàng theo storeId
		Optional<Store> storeOptional = storeRepository.findById(storeId);

		if (!storeOptional.isPresent()) {
			model.addAttribute("error", "Cửa hàng không tồn tại");
			return "error";
		}

		Store store = storeOptional.get();
		String ownerId = store.getOwnerId();

		// Tìm thực đơn của cửa hàng
		List<StoreMenu> storeMenus = storeMenuService.findByStore_StoreId(storeId);
		model.addAttribute("menulist", storeMenus);
		model.addAttribute("storeId", storeId);

		// Tạo breadcrumb
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + ownerId));
		breadcrumbs.add(new Breadcrumb("Cửa hàng của tôi", "/stores/" + ownerId));
		breadcrumbs.add(new Breadcrumb("Menu của tôi", "/menu/" + storeId));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/menu";
	}

	// Thêm thực đơn cho cửa hàng
	@PostMapping("/menu/add")
	public String addMenu(@RequestParam("storeId") String storeId, @RequestParam("foodFirst") String foodFirst,
			@RequestParam("foodMain") String foodMain, @RequestParam("foodDessert") String foodDessert, Model model) {

		// Kiểm tra xem store có tồn tại không
		Optional<Store> storeOptional = storeRepository.findById(storeId);
		if (!storeOptional.isPresent()) {
			model.addAttribute("error", "Cửa hàng không tồn tại");
			return "error";
		}

		Store store = storeOptional.get();
		StoreMenu newMenu = new StoreMenu();
		newMenu.setStore(store);
		newMenu.setFoodFirst(foodFirst);
		newMenu.setFoodMain(foodMain);
		newMenu.setFoodDessert(foodDessert);

		// Lưu thực đơn vào cơ sở dữ liệu
		storeMenuService.save(newMenu);

		return "redirect:/stores/menu?storeId=" + storeId;
	}

	@GetMapping("/menu/edit")
	public String showEditMenuForm(@RequestParam("menuId") String menuId, Model model) {
		Optional<StoreMenu> storeMenuOptional = storeMenuService.findById(menuId);

		if (storeMenuOptional.isPresent()) {
			StoreMenu storeMenu = storeMenuOptional.get();
			model.addAttribute("menu", storeMenu);
			return "stores/edit_menu";
		} else {
			model.addAttribute("error", "Không tìm thấy thực đơn");
			return "error";
		}
	}

	// Cập nhật thực đơn của cửa hàng
	@PostMapping("/menu/update")
	public String updateStoreMenu(@RequestParam("menuId") String menuId, @RequestParam("foodFirst") String foodFirst,
			@RequestParam("foodMain") String foodMain, @RequestParam("foodDessert") String foodDessert, Model model) {

		StoreMenu updatedMenu = storeMenuService.updateStoreMenu(menuId, foodFirst, foodMain, foodDessert);
		String storeId = updatedMenu.getStore().getStoreId();

		return "redirect:/stores/menu?storeId=" + storeId;
	}

	// Xóa thực đơn của cửa hàng
	@PostMapping("/menu/delete")
	public String deleteStoreMenu(@RequestParam("menuId") String menuId, Model model) {

		StoreMenu deletedMenu = storeMenuService.deleteStoreMenu(menuId);
		String storeId = deletedMenu.getStore().getStoreId();

		return "redirect:/stores/menu?storeId=" + storeId;
	}

// Food--------------------------------------------------------------------------------   

	@GetMapping("/food")
	public String getStoreFood(@RequestParam("storeId") String storeId, Model model) {
		// Tìm cửa hàng theo storeId
		Optional<Store> storeOptional = storeRepository.findById(storeId);

		if (!storeOptional.isPresent()) {
			model.addAttribute("error", "Cửa hàng không tồn tại");
			return "error";
		}

		Store store = storeOptional.get();
		String ownerId = store.getOwnerId();

		List<StoreFood> storeFood = storeFoodService.findByStore_StoreId(storeId);
		
		for (StoreFood food : storeFood) {
            food.getFormattedPrice();
        }
		
		model.addAttribute("foodlist", storeFood);
		model.addAttribute("storeId", storeId);

		// Tạo breadcrumb
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + ownerId));
		breadcrumbs.add(new Breadcrumb("Cửa hàng của tôi", "/stores/" + ownerId));
		breadcrumbs.add(new Breadcrumb("Món ăn", "/food/" + storeId));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/food";
	}

	@PostMapping("/food/add")
	public String addFood(@RequestParam("storeId") String storeId,
	                      @RequestParam("foodName") String foodName,
	                      @RequestParam("price") BigDecimal price,
	                      @RequestParam("food_images") MultipartFile[] foodImages, 
	                      Model model) {

	    Optional<Store> storeOptional = storeRepository.findById(storeId);
	    if (!storeOptional.isPresent()) {
	        model.addAttribute("error", "Cửa hàng không tồn tại");
	        return "error";
	    }

	    Store store = storeOptional.get();
	    StoreFood newFood = new StoreFood();
	    newFood.setStore(store);
	    newFood.setFoodName(foodName);
	    newFood.setPrice(price);

	    storeFoodService.save(newFood);
	    
	    if (foodImages != null && foodImages.length > 0) {
	        for (MultipartFile image : foodImages) {
	            if (!image.isEmpty()) {
	                Path tempFilePath = null;

	                try {
	                    // Lưu tạm thời file ảnh
	                    tempFilePath = saveFile(image);

	                    // Tải lên Imgur và lấy URL ảnh
	                    String imageUrl = storeFoodService.uploadImageToImgur(tempFilePath.toString());

	                    // Tạo đối tượng Photo và liên kết với món ăn
	                    Photo photo = new Photo();
	                    photo.setPhotoUrl(imageUrl);
	                    photo.setFoodId(newFood.getFoodId());
	                    photo.setPhotoType("food");

	                    storeFoodService.savePhoto(photo);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return "redirect:/register-store?error=upload_failed";
	                } finally {
	                    // Xóa file tạm thời
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

	    return "redirect:/stores/food?storeId=" + storeId;
	}



	@GetMapping("/food/edit")
	public String showEditFoodForm(@RequestParam("foodId") String foodId, Model model) {
		Optional<StoreFood> storeMenuOptional = storeFoodService.findById(foodId);

		if (storeMenuOptional.isPresent()) {
			StoreFood storeFood = storeMenuOptional.get();
			model.addAttribute("food", storeFood);
			return "stores/edit_food";
		} else {
			model.addAttribute("error", "Không tìm thấy món ăn");
			return "error";
		}
	}

	// Cập nhật thực đơn của cửa hàng
	@PostMapping("/food/update")
	public String updateStoreMenu(@RequestParam("foodId") String foodId, @RequestParam("foodName") String foodName,
			@RequestParam("price") BigDecimal price, Model model) {

		StoreFood updateFood = storeFoodService.updateStoreFood(foodId, foodName, price);
		String storeId = updateFood.getStore().getStoreId();

		return "redirect:/stores/food?storeId=" + storeId;
	}

	// Xóa thực đơn của cửa hàng
	@PostMapping("/food/delete")
	public String deleteStoreFood(@RequestParam("foodId") String foodId, Model model) {
		String storeId = storeFoodService.deleteStoreFood(foodId); // Nhận storeId từ phương thức
		return "redirect:/stores/food?storeId=" + storeId;
	}

//Dinkkkkkkkkkkkkkkk--------------------------------------------------------------------

	@GetMapping("/drink")
	public String getStoreDrink(@RequestParam("storeId") String storeId, Model model) {
		// Tìm cửa hàng theo storeId
		Optional<Store> storeOptional = storeRepository.findById(storeId);

		if (!storeOptional.isPresent()) {
			model.addAttribute("error", "Cửa hàng không tồn tại");
			return "error";
		}

		Store store = storeOptional.get();
		String ownerId = store.getOwnerId();

		List<StoreDrink> storeDrinks = storeDrinkService.findByStore_StoreId(storeId);
		
		for (StoreDrink drink : storeDrinks) {
            drink.getFormattedPrice();
        }
		
		model.addAttribute("drinklist", storeDrinks);
		model.addAttribute("storeId", storeId);

		// Tạo breadcrumb
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + ownerId));
		breadcrumbs.add(new Breadcrumb("Cửa hàng của tôi", "/stores/" + ownerId));
		breadcrumbs.add(new Breadcrumb("Đồ uống", "/drink/" + storeId));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/drink";
	}
	

	@PostMapping("/drink/add")
	public String addDrink(@RequestParam("storeId") String storeId,
	                       @RequestParam("drinkName") String drinkName,
	                       @RequestParam("price") BigDecimal price,
	                       @RequestParam("drink_images") MultipartFile[] drinkImages, 
	                       Model model) {

	    Optional<Store> storeOptional = storeRepository.findById(storeId);
	    if (!storeOptional.isPresent()) {
	        model.addAttribute("error", "Cửa hàng không tồn tại");
	        return "error";
	    }

	    Store store = storeOptional.get();
	    StoreDrink newDrink = new StoreDrink();
	    newDrink.setStoreId(storeId);
	    newDrink.setDrinkName(drinkName);
	    newDrink.setPrice(price);

	    storeDrinkService.save(newDrink);
	    
	    if (drinkImages != null && drinkImages.length > 0) {
	        for (MultipartFile image : drinkImages) {
	            if (!image.isEmpty()) {
	                Path tempFilePath = null;

	                try {
	                    // Lưu tạm thời file ảnh
	                    tempFilePath = saveFile(image);

	                    // Tải lên Imgur và lấy URL ảnh
	                    String imageUrl = storeDrinkService.uploadImageToImgur(tempFilePath.toString());

	                    // Tạo đối tượng Photo và liên kết với thức uống
	                    Photo photo = new Photo();
	                    photo.setPhotoUrl(imageUrl);
	                    photo.setDrinkId(newDrink.getDrinkId());
	                    photo.setPhotoType("drink");

	                    storeFoodService.savePhoto(photo);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    return "redirect:/register-store?error=upload_failed";
	                } finally {
	                    // Xóa file tạm thời
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

	    return "redirect:/stores/drink?storeId=" + storeId;
	}



	@GetMapping("/drink/edit")
	public String showEditDrinkForm(@RequestParam("drinkId") String drinkId, Model model) {
		Optional<StoreDrink> storeMenuOptional = storeDrinkService.findById(drinkId);

		if (storeMenuOptional.isPresent()) {
			StoreDrink storeDrink = storeMenuOptional.get();
			model.addAttribute("drink", storeDrink);
			return "stores/edit_drink";
		} else {
			model.addAttribute("error", "Không tìm thấy món ăn");
			return "error";
		}
	}

	// Cập nhật thực đơn của cửa hàng
	@PostMapping("/drink/update")
	public String updateStoreDrink(@RequestParam("drinkId") String drinkId, @RequestParam("drinkName") String drinkName,
			@RequestParam("price") BigDecimal price, Model model) {

		StoreDrink updateDrink = storeDrinkService.updateStoreDrink(drinkId, drinkName, price);
		String storeId = updateDrink.getStore().getStoreId();

		return "redirect:/stores/drink?storeId=" + storeId;
	}

	// Xóa thực đơn của cửa hàng
	@PostMapping("/drink/delete")
	public String deleteStoreDrink(@RequestParam("drinkId") String drinkId, Model model) {

		StoreDrink deleteDrink = storeDrinkService.deleteStoreDrink(drinkId);
		String storeId = deleteDrink.getStore().getStoreId();

		return "redirect:/stores/drink?storeId=" + storeId;
	}

}
