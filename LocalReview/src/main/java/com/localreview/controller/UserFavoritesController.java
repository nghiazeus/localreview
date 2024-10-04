package com.localreview.controller;

import com.localreview.entity.UserFavorites;
import com.localreview.entity.User;
import com.localreview.entity.Store;
import com.localreview.service.StoreService;
import com.localreview.service.UserFavoritesService;
import com.localreview.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserFavoritesController {

	@Autowired
	private UserService userService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private UserFavoritesService userFavoritesService;

	@PostMapping("/add-favorite")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addFavorite(@RequestParam("storeId") String storeId,
	        @AuthenticationPrincipal UserDetails userDetails) {
	    Map<String, Object> response = new HashMap<>();

	    if (userDetails == null) {
	        response.put("success", false);
	        response.put("message", "Bạn cần đăng nhập để thêm cửa hàng yêu thích.");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	    }

	    String userEmail = userDetails.getUsername();
	    User currentUser = userService.findByEmail(userEmail);

	    Store store = storeService.findById(storeId);
	    if (store == null) {
	        response.put("success", false);
	        response.put("message", "Không tìm thấy cửa hàng.");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    UserFavorites favorite = userFavoritesService.findByUserAndStore(currentUser, store);
	    if (favorite != null) {
	        response.put("success", false);
	        response.put("isFavorited", true);
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	    }

	    UserFavorites newFavorite = new UserFavorites();
	    newFavorite.setUser(currentUser);
	    newFavorite.setStore(store);
	    userFavoritesService.save(newFavorite);

	    response.put("success", true);
	    response.put("isFavorited", true);
	    response.put("favoriteCount", store.getFavoriteCount()); // Trả về số lượng yêu thích
	    return ResponseEntity.ok(response);
	}

	@PostMapping("/remove-favorite")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> removeFavorite(@RequestParam("storeId") String storeId,
	        @AuthenticationPrincipal UserDetails userDetails) {
	    Map<String, Object> response = new HashMap<>();

	    if (userDetails == null) {
	        response.put("success", false);
	        response.put("message", "Bạn cần đăng nhập để xóa cửa hàng yêu thích.");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	    }

	    String userEmail = userDetails.getUsername();
	    User currentUser = userService.findByEmail(userEmail);

	    Store store = storeService.findById(storeId);
	    if (store == null) {
	        response.put("success", false);
	        response.put("message", "Không tìm thấy cửa hàng.");
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }

	    UserFavorites favorite = userFavoritesService.findByUserAndStore(currentUser, store);
	    if (favorite != null) {
	        userFavoritesService.delete(favorite);
	        response.put("success", true);
	        response.put("isFavorited", false);
	        response.put("favoriteCount", store.getFavoriteCount()); // Trả về số lượng yêu thích
	    } else {
	        response.put("success", false);
	        response.put("isFavorited", true);
	    }
	    return ResponseEntity.ok(response);
	}

}
