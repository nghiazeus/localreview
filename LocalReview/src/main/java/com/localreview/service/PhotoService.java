package com.localreview.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.localreview.entity.Photo;

public interface PhotoService {
    Photo savePhoto(Photo photo);

    List<Photo> getPhotosByReviewId(String reviewId);
    
    String uploadImageToImgur(String imagePath);
    
    Path saveFile(MultipartFile file) throws IOException;
}
