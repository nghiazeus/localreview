package com.localreview.service;

import java.util.List;
import java.util.Optional;

import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;

public interface StoreMenuService {

	List<StoreMenu> getMenuByStoreId(String storeId);

	StoreMenu getMenuById(String menuId);

	StoreMenu saveMenu(StoreMenu menu);

	void deleteMenu(String menuId);

}
