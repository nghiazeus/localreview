package com.localreview.serviceiml;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Photo;
import com.localreview.repository.PhotoRepository;
import com.localreview.service.PhotoService;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public Photo savePhoto(Photo photo) {
        // Logic để lưu ảnh
        return photoRepository.save(photo);
    }

    @Override
    public List<Photo> getPhotosByReviewId(String reviewId) {
        // Logic để lấy danh sách ảnh liên kết với review
        return photoRepository.findByReviewId(reviewId);
    }
}
