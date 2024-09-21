package com.localreview.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.localreview.entity.Photo;
import com.localreview.entity.Store;
import com.localreview.entity.StoreDrink;

public interface StoreDrinkService {

	List<StoreDrink> findByStore_StoreId(String storeId);

	StoreDrink updateStoreDrink(String drinkId, String drinkName, BigDecimal price);

	StoreDrink deleteStoreDrink(String drinkId);

	void save(StoreDrink newDrink);

	Optional<StoreDrink> findById(String drinkId);
	
	String uploadImageToImgur(String imagePath);

	Photo savePhoto(Photo photo);

}
