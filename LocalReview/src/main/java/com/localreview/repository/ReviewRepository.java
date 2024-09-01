package com.localreview.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, String> {
    // Sửa phương thức để truy cập thuộc tính storeId của store
    List<Review> findByStore_StoreId(String storeId);
}