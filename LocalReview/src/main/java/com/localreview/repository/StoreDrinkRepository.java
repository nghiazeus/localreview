package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.localreview.entity.StoreDrink;

public interface StoreDrinkRepository extends JpaRepository<StoreDrink, String> {
	
	List<StoreDrink> findByStore_StoreId(String storeId);

}
