package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localreview.entity.StoreFood;

public interface StoreFoodRepository extends JpaRepository<StoreFood, String> {
	
	List<StoreFood> findByStore_StoreId(String storeId);

}
