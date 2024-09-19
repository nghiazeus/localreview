package com.localreview.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import com.localreview.entity.Photo;
import com.localreview.entity.Review;

public interface PhotoService {
    Photo savePhoto(Photo photo);

    List<Photo> getPhotosByReviewId(String reviewId);
    
    List<Photo> getPhotosByStoreId(String storeId);
    
    String uploadImageToImgur(String imagePath);
    
    Path saveFile(MultipartFile file) throws IOException;

    
}
