package com.localreview.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.localreview.entity.Categories;
import com.localreview.entity.Store;
import com.localreview.entity.StoreDrink;
import com.localreview.entity.StoreFood;
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;
import com.localreview.entity.Breadcrumb; // Giả sử bạn có một lớp Breadcrumb
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

	@GetMapping("/{userId}")
	public String getUserStores(@PathVariable("userId") String userId, @RequestParam(required = false) String storeId,
			Model model) {
		User currentUser = userRepository.findById(userId).orElse(null);
		if (currentUser == null) {
			model.addAttribute("error", "Không tìm thấy người dùng");
			return "error";
		}

		List<Store> stores = storeService.getStoresByOwnerId(userId);

		model.addAttribute("stores", stores);

		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + userId));
		breadcrumbs.add(new Breadcrumb("Cửa hàng của tôi", storeId));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/user_stores";
	}

	@PostMapping("/updatestoreuser")
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
			model.addAttribute("success", "Cập nhật cửa hàng thành công");

			return "redirect:/stores/" + existingStore.getOwnerId();
		} else {
			model.addAttribute("error", "Không tìm thấy cửa hàng");
			return "error";
		}
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

		// Tìm thực đơn của cửa hàng
		List<StoreFood> storeFood = storeFoodService.findByStore_StoreId(storeId);
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
	public String addFood(@RequestParam("storeId") String storeId, @RequestParam("foodName") String foodName,
			@RequestParam("price") BigDecimal price, Model model) {

		Optional<Store> storeOptional = storeRepository.findById(storeId);
		if (!storeOptional.isPresent()) {
			model.addAttribute("error", "Cửa hàng không tồn tại");
			return "error";
		}

		Store store = storeOptional.get();
		StoreFood newFood = new StoreFood();
		newFood.setStore(store); // Thiết lập Store cho StoreFood
		newFood.setFoodName(foodName);
		newFood.setPrice(price);

		storeFoodService.save(newFood);

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

		// Tìm danh sách thức uống của cửa hàng
		List<StoreDrink> storeDrinks = storeDrinkService.findByStore_StoreId(storeId);
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
