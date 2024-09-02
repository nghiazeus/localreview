package com.localreview.controller;

import com.localreview.entity.Photo;
import com.localreview.entity.Review;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.service.ReviewService;
import com.localreview.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    // Lưu một review mới, bao gồm nhiều ảnh nếu có
    @PostMapping("/{storeId}")
    public ResponseEntity<Review> saveReview(
        @PathVariable("storeId") String storeId,
        @RequestParam("comment") String comment,
        @RequestParam("rating") Integer rating,
        @RequestParam(value = "images", required = false) MultipartFile[] images,
        Principal principal) {

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);

        // Lấy Store từ storeId
        Store store = reviewService.getStoreById(storeId);
        if (store == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        review.setStore(store);

        // Lấy thông tin người dùng đang đăng nhập
        String currentID;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            currentID = (String) attributes.get("email"); // Lấy email từ OAuth2 user
        } else {
            currentID = authentication.getName(); // Sử dụng tên người dùng từ phương thức đăng nhập thông thường
        }

        // Kiểm tra sự tồn tại của người dùng và lấy thông tin người dùng
        User currentUser = userService.findByEmail(currentID);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        review.setUser(currentUser);

        // Lưu review trước
        Review savedReview = reviewService.saveReview(review);

        // Xử lý ảnh sau khi review đã được lưu
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    Path tempFilePath = null;

                    try {
                        // Lưu file tạm thời
                        tempFilePath = saveFile(image);

                        // Upload lên Imgur và lấy URL
                        String imageUrl = reviewService.uploadImageToImgur(tempFilePath.toString());

                        // Tạo đối tượng Photo và lưu vào cơ sở dữ liệu
                        Photo photo = new Photo();
                        photo.setPhotoUrl(imageUrl);
                        photo.setReviewId(savedReview.getReviewId()); // Gán reviewId của review đã lưu
                        photo.setStoreId(storeId); // Gán storeId nếu cần thiết

                        reviewService.savePhoto(photo);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

        return ResponseEntity.ok(savedReview);
    }


    // Lấy danh sách review theo storeId
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Review>> getReviewsByStore(@PathVariable String storeId) {
        List<Review> reviews = reviewService.getReviewsByStore(storeId);
        return ResponseEntity.ok(reviews);
    }

    // Phương thức hỗ trợ để lưu file tạm thời
    private Path saveFile(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());
        return tempFile;
    }
}
