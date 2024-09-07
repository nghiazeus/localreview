package com.localreview.controller;

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
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;
import com.localreview.entity.Breadcrumb; // Giả sử bạn có một lớp Breadcrumb
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

    @Autowired
    private StoreService storeService;

    @Autowired
    private CategoriesRepository categoryRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private StoreMenuService menuService;
    
    @Autowired
    private StoreMenuRepository menuRepository;

    @GetMapping("/{userId}")
    public String getUserStores(@PathVariable("userId") String userId, @RequestParam(required = false) String storeId, Model model) {
        User currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null) {
            model.addAttribute("error", "Không tìm thấy người dùng");
            return "error";
        }

        // Nếu storeId không null, lấy danh sách thực đơn của cửa hàng đó
      //  List<StoreMenu> menuList = storeId != null ? menuService.getMenuByStoreId(storeId) : new ArrayList<>();
        List<Store> stores = storeService.getStoresByOwnerId(userId);
        
        List<StoreMenu> menulist = menuService.getMenuByStoreId(userId);

        model.addAttribute("menulist", menulist);
        model.addAttribute("stores", stores);

        // Thêm breadcrumb vào model
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
        breadcrumbs.add(new Breadcrumb("Tài khoản", "/profile/" + userId));
        breadcrumbs.add(new Breadcrumb("Cửa hàng của tôi", "/stores/" + userId));
        model.addAttribute("breadcrumbs", breadcrumbs);

        return "user_stores";
    }

//    @GetMapping("/edit/{id}")
//    public String editStore(@PathVariable String id, Model model) {
//        Store store = storeRepository.findById(id).orElse(null);
//        List<Categories> categories = categoryRepository.findAll();
//        model.addAttribute("store", store);
//        model.addAttribute("categories", categories);
//        return "edit_store"; // Tên của file Thymeleaf
//    }

    @PostMapping("/update")
    public String updateStore(@RequestParam String storeId,
                               @RequestParam String storeName,
                               @RequestParam String storeCategory,
                               @RequestParam String address,
                               @RequestParam String phoneNumber,
                               RedirectAttributes redirectAttributes) {
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isPresent()) {
            Store store = storeOptional.get();
            store.setStoreName(storeName);
            // Assume you have a way to fetch categories
            Categories category = categoryRepository.findById(storeCategory).orElse(null);
            store.setStoreCategories(category);
            String[] addressParts = address.split(", ");
            store.setAddressStreet(addressParts[0]);
            store.setAddressCommune(addressParts[1]);
            store.setAddressDistrict(addressParts[2]);
            store.setAddressCity(addressParts[3]);
            store.setPhoneNumber(phoneNumber);
            
            storeRepository.save(store);

            redirectAttributes.addFlashAttribute("message", "Cập nhật cửa hàng thành công");
            return "redirect:/stores/" + store.getOwnerId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Cửa hàng không tồn tại");
            return "redirect:/stores";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStore(@PathVariable String id) {
        Store store = storeRepository.findById(id).orElse(null);
        if (store != null) {
            storeService.deleteStore(id);
        }
        return "redirect:/stores/" + store.getOwnerId();
    }

    // Thêm menu
    @PostMapping("/menu/update")
    public String updateMenu(@RequestParam String menuId,
                             @RequestParam String foodFirst,
                             @RequestParam String foodMain,
                             @RequestParam String foodDessert,
                             RedirectAttributes redirectAttributes) {
        StoreMenu menu = menuService.getMenuById(menuId);

        if (menu != null) {
            menu.setFoodFirst(foodFirst);
            menu.setFoodMain(foodMain);
            menu.setFoodDessert(foodDessert);
            menuService.saveMenu(menu);

            redirectAttributes.addFlashAttribute("message", "Cập nhật menu thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Menu không tồn tại");
        }

        return "redirect:/stores/" + menu.getStore().getOwnerId() + "/menu";
    }

    @PostMapping("/menu/add")
    public String addMenu(@RequestParam String storeId,
                          @RequestParam String foodFirst,
                          @RequestParam String foodMain,
                          @RequestParam String foodDessert,
                          RedirectAttributes redirectAttributes) {
        Store store = storeRepository.findById(storeId).orElse(null);

        if (store != null) {
            StoreMenu newMenu = new StoreMenu();
            newMenu.setStore(store);
            newMenu.setFoodFirst(foodFirst);
            newMenu.setFoodMain(foodMain);
            newMenu.setFoodDessert(foodDessert);

            menuService.saveMenu(newMenu);
            redirectAttributes.addFlashAttribute("message", "Thêm menu thành công");
        } else {
            redirectAttributes.addFlashAttribute("error", "Cửa hàng không tồn tại");
        }

        return "redirect:/stores/" + storeId + "/menu";
    }
}
