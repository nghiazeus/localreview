package com.localreview.service;

import java.util.List;

import com.localreview.DTO.ReviewWithPhotosDTO;
import com.localreview.entity.Photo;
import com.localreview.entity.Review;
import com.localreview.entity.Store;
import com.localreview.entity.User;

public interface ReviewService {
    Review saveReview(Review review);

    List<Review> getReviewsByStore(String storeId);

    String uploadImageToImgur(String imagePath); // Phương thức upload ảnh
    
    Photo savePhoto(Photo photo);
    
    User getUserByEmail(String email);
    
    Store getStoreById(String storeId); // Thêm phương thức để lấy Store
    
    List<ReviewWithPhotosDTO> getReviewsWithPhotosByStoreId(String storeId);
    
    
}
