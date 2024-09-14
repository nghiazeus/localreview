package com.localreview.serviceiml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.StoreFood;
import com.localreview.repository.StoreFoodRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreFoodService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StoreFoodServiceImpl implements StoreFoodService {

	@Autowired
	private StoreFoodRepository storeFoodRepository;

	@Override
	public List<StoreFood> findByStore_StoreId(String storeId) {
		return storeFoodRepository.findByStore_StoreId(storeId);
	}

	@Override
	public StoreFood updateStoreFood(String foodId, String foodName, BigDecimal price) {
	    StoreFood existingMenu = storeFoodRepository.findById(foodId).orElse(null);

	    if (existingMenu == null) {
	        throw new IllegalArgumentException("Món ăn không tồn tại");
	    }

	    existingMenu.setFoodName(foodName);
	    existingMenu.setPrice(price);

	    return storeFoodRepository.save(existingMenu);
	}


	@Override
	public String deleteStoreFood(String foodId) {
	    StoreFood food = storeFoodRepository.findById(foodId).orElseThrow(() -> 
	        new IllegalArgumentException("Món ăn không tồn tại"));

	    String storeId = food.getStore().getStoreId();  // Lấy storeId trước khi xóa
	    storeFoodRepository.delete(food);
	    return storeId;  // Trả về storeId sau khi món ăn đã bị xóa
	}


	@Override
	public void save(StoreFood newFood) {
		storeFoodRepository.save(newFood);

	}

	@Override
	public Optional<StoreFood> findById(String foodId) {
		return storeFoodRepository.findById(foodId);
	}

}