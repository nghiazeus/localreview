package com.localreview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findByStore_StoreId(String storeId);
    
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.photos WHERE r.store.storeId = :storeId")
    List<Review> findReviewsWithPhotosByStoreId(@Param("storeId") String storeId);
    
}