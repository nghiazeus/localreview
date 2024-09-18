package com.localreview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.localreview.entity.Photo;
import com.localreview.entity.Review;

public interface PhotoRepository extends JpaRepository<Photo, String> {
	
	List<Photo> findByReviewId(String reviewId);
	
	List<Photo> findByStoreId(String storeId);
	
	/*
	 * @Query("SELECT r FROM Review r WHERE r.store.storeId = :storeId")
	 * List<Review> findAllByStoreId(@Param("storeId") String storeId);
	 */
}
