package com.localreview.controller;

import com.localreview.entity.Blacklist;
import com.localreview.entity.Photo;
import com.localreview.entity.Review;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.BlacklistRepository;
import com.localreview.repository.ReviewReportRepository;
import com.localreview.repository.ReviewRepository;
import com.localreview.service.ReviewService;
import com.localreview.service.UserService;
import com.localreview.serviceiml.NotificationService;

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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BlacklistRepository blacklistRepository;
    
    @Autowired
    private ReviewReportRepository reviewReportRepository;
    
    @Autowired
    private NotificationService notificationService;

    // Lưu một review mới, bao gồm nhiều ảnh nếu có
    @PostMapping("/{storeId}")
    public ResponseEntity<?> saveReview(
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
        
        // Kiểm tra nếu người dùng có trong blacklist và is_active là true, không cho đánh giá
        Optional<Blacklist> blacklistEntryOpt = blacklistRepository.findByUserAndIsActiveTrue(currentUser);
        if (blacklistEntryOpt.isPresent()) {
            Blacklist blacklistEntry = blacklistEntryOpt.get();
            
            // Kiểm tra ngày hết hạn và cập nhật trạng thái
            LocalDateTime now = LocalDateTime.now();
            if (blacklistEntry.getExpiresAt().isBefore(now)) {
                blacklistEntry.setActive(false); // Đặt isActive thành false
                blacklistRepository.save(blacklistEntry); // Lưu thay đổi
                // Sau khi người dùng hết hạn cấm, cập nhật trạng thái review reports
                reviewReportRepository.deactivateReportsByUserId(currentUser.getUserId());
            } else if (blacklistEntry.isActive()) { // Nếu is_active vẫn true, không cho phép đánh giá
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Bạn đã bị cấm")); // Trả về thông báo lỗi
            }
        }


        // Lưu review trước
        Review savedReview = reviewService.saveReview(review);
        
     // Lấy thông tin người dùng A (chủ cửa hàng)
        String ownerId = store.getOwnerId(); // Lấy userId của chủ cửa hàng
        User owner = userService.findByUserId(ownerId); // Tìm User dựa trên userId

        if (owner != null) {
            // Gửi thông báo cho người dùng A
            String message = "Người dùng " + currentUser.getName() + " đã đánh giá cửa hàng của bạn.";
            notificationService.sendRealTimeNotification(message, "new_review", owner.getUserId()); // Gửi thông báo
        }

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
                        photo.setPhotoType("review"); // Gán loại ảnh là review

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