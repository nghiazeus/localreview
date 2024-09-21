package com.localreview.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.localreview.entity.Photo;
import com.localreview.entity.Store;
import com.localreview.entity.StoreFood;

public interface StoreFoodService {

	List<StoreFood> findByStore_StoreId(String storeId);

	StoreFood updateStoreFood(String foodId, String foodName, BigDecimal price);

	String deleteStoreFood(String foodId);

	void save(StoreFood newFood);

	Optional<StoreFood> findById(String foodId);

	String uploadImageToImgur(String imagePath);

	Photo savePhoto(Photo photo);

}
