package com.localreview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.localreview.entity.Categories;
import com.localreview.entity.Photo;
import com.localreview.entity.Review;
import com.localreview.entity.Store;
import com.localreview.entity.StoreFood;
import com.localreview.entity.User;
import com.localreview.repository.CategoriesRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.PhotoService;
import com.localreview.service.ReviewService;
import com.localreview.service.StoreFoodService;
import com.localreview.service.StoreService;
import com.localreview.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private StoreService storeService;
    
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoriesRepository category;
    
    @Autowired
    private StoreFoodService storeFoodService;
    
    private final ReviewService reviewService;
    private final PhotoService photoService;

    public GlobalControllerAdvice(ReviewService reviewService, PhotoService photoService) {
        this.reviewService = reviewService;
        this.photoService = photoService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // Lấy thông tin xác thực người dùng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            currentEmail = userDetails.getUsername();
        } else if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            currentEmail = oauthUser.getAttribute("email");
        }

        if (currentEmail != null) {
            User currentUser = userService.findByEmail(currentEmail);
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
            } else {
                model.addAttribute("error", "Không tìm thấy thông tin người dùng.");
            }
        }

        // Thêm danh sách cửa hàng và categories vào model
        List<Store> randomStores = storeService.getRandomStores();
        List<Categories> listscategori = category.findAll();
        model.addAttribute("stores", randomStores);
        model.addAttribute("categories", listscategori);
    }
    
    
//    private void addReviews(@PathVariable("storeId") String storeId, Model model) {
//        try {
//            List<Review> reviews = reviewService.getReviewsByStore(storeId);
//            Map<String, List<String>> reviewPhotosMap = new HashMap<>();
//
//            if (reviews != null && !reviews.isEmpty()) {
//                reviewPhotosMap = reviews.stream()
//                    .collect(Collectors.toMap(Review::getReviewId,
//                        review -> photoService.getPhotosByReviewId(review.getReviewId())
//                            .stream().map(Photo::getPhotoUrl)
//                            .collect(Collectors.toList())));
//                model.addAttribute("reviews", reviews); // Thêm vào model với tên "reviews"
//                model.addAttribute("reviewPhotosMap", reviewPhotosMap);
//            } else {
//                model.addAttribute("reviews", Collections.emptyList()); // Đảm bảo bạn thêm danh sách rỗng nếu không có đánh giá
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("error", "Đã xảy ra lỗi khi lấy thông tin đánh giá.");
//        }
//    }

}
