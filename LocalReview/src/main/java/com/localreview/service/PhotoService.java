package com.localreview.service;

import java.util.List;

import com.localreview.entity.Photo;

public interface PhotoService {
    Photo savePhoto(Photo photo);

    List<Photo> getPhotosByReviewId(String reviewId);
}
