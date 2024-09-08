package com.localreview.serviceiml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreMenuService;

import java.util.List;

@Service
public class StoreMenuServiceImpl implements StoreMenuService {

	@Autowired
	private StoreMenuRepository menuRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Override
	public List<StoreMenu> getMenuByStoreId(String storeId) {

		Store store = storeRepository.findById(storeId).orElse(null);
		if (store != null) {
			return menuRepository.findByStore_StoreId(storeId);
		} else {
			return null;
		}
	}

	@Override
	public StoreMenu getMenuById(String menuId) {
		return menuRepository.findById(menuId).orElse(null);
	}

	@Override
	public StoreMenu saveMenu(StoreMenu menu) {
		return menuRepository.save(menu);
	}

	@Override
	public void deleteMenu(String menuId) {
		menuRepository.deleteById(menuId);
	}
}