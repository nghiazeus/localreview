package com.localreview.serviceiml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.StoreDrink;
import com.localreview.entity.StoreFood;
import com.localreview.entity.StoreMenu;
import com.localreview.repository.StoreDrinkRepository;
import com.localreview.repository.StoreFoodRepository;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreDrinkService;
import com.localreview.service.StoreFoodService;
import com.localreview.service.StoreMenuService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StoreDinkServiceImpl implements StoreDrinkService {

	@Autowired
	private StoreDrinkRepository storeDrinkRepository;

	@Override
	public List<StoreDrink> findByStore_StoreId(String storeId) {
		return storeDrinkRepository.findByStore_StoreId(storeId);
	}

	@Override
	public StoreDrink updateStoreDrink(String drinkId, String drinkName, BigDecimal price) {
		StoreDrink existingMenu = storeDrinkRepository.findById(drinkId).orElse(null);

		if (existingMenu == null) {
			throw new IllegalArgumentException("Món ăn không tồn tại");
		}

		existingMenu.setDrinkName(drinkName);
		existingMenu.setPrice(price);

		return storeDrinkRepository.save(existingMenu);
	}

	@Override
	public StoreDrink deleteStoreDrink(String drinkId) {
		StoreDrink drink = storeDrinkRepository.findById(drinkId).orElse(null);

		if (drink == null) {
			throw new IllegalArgumentException("Món ăn không tồn tại");
		}
		storeDrinkRepository.delete(drink);
		return drink;
	}

	@Override
	public void save(StoreDrink newDrink) {
		storeDrinkRepository.save(newDrink);

	}

	@Override
	public Optional<StoreDrink> findById(String drinkId) {
		return storeDrinkRepository.findById(drinkId);
	}

}