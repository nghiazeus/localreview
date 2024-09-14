package com.localreview.service;

import java.util.List;
import java.util.Optional;

import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;

public interface StoreMenuService {

	List<StoreMenu> findByStore_StoreId(String storeId);

	StoreMenu updateStoreMenu(String menuId, String foodFirst, String foodMain, String foodDessert);

	StoreMenu deleteStoreMenu(String menuId);

	void save(StoreMenu newMenu);

	Optional<StoreMenu> findById(String menuId);

}
