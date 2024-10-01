package com.localreview.controller;

import com.localreview.entity.Breadcrumb;
import com.localreview.entity.SearchHistory;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.UserRepository;
import com.localreview.service.SearchHistoryService;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class SearchHistoryController {

	@Autowired
	private SearchHistoryService searchHistoryService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private UserService userService;

	@GetMapping("/store/search")
	public String searchStores(@RequestParam("query") String query, Model model) {
	    // Tìm kiếm cửa hàng
	    List<Store> searchResults = storeService.searchStores(query);
	    model.addAttribute("stores", searchResults);

	    // Lấy thông tin người dùng từ SecurityContext
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User currentUser = null;
	    
	    if (authentication != null && authentication.isAuthenticated()) {
	        String currentEmail = authentication.getName(); // Lấy email người dùng
	        currentUser = userService.findByEmail(currentEmail); // Tìm người dùng theo email
	    }

	    // Lưu lịch sử tìm kiếm nếu người dùng đã đăng nhập
	    if (currentUser != null) {
	        SearchHistory searchHistory = new SearchHistory();
	        searchHistory.setUser(currentUser);
	        searchHistory.setSearchTerm(query);
	        searchHistoryService.saveSearchHistory(searchHistory);
	    }

		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("Trang chủ", "/index"));
		breadcrumbs.add(new Breadcrumb("Cửa hàng", "/store"));
		breadcrumbs.add(new Breadcrumb("Tìm kiếm", "/store/search?query=" + query));
		breadcrumbs.add(new Breadcrumb("" + query, "/store/search?query= " + query));
		model.addAttribute("breadcrumbs", breadcrumbs);

		return "stores/stores";
	}
	
	@PostMapping("/search/history/delete")
	public ResponseEntity<Void> deleteSearchHistory(@RequestParam("historyId") String historyId) {
	    searchHistoryService.deleteSearchHistoryById(historyId);
	    return ResponseEntity.ok().build();
	}
	

	@GetMapping("/api/store/suggestions")
    public ResponseEntity<List<String>> getStoreSuggestions(@RequestParam String keyword) {
        List<String> suggestions = storeService.findStoreNamesByKeyword(keyword);
        return ResponseEntity.ok(suggestions);
    }

}
