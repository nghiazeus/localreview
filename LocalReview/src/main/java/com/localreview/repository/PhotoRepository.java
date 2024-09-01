package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localreview.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, String> {
	List<Photo> findByReviewId(String reviewId);
}
