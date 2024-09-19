package com.localreview.serviceiml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;
import com.localreview.entity.User;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreMenuService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoreMenuServiceImpl implements StoreMenuService {

	@Autowired
	private StoreMenuRepository storeMenuRepository;
	
	@Autowired
	private StoreRepository storeRepository;

	/*
	 * @Autowired private StoreRepository storeRepository;
	 */

	@Override
	public List<StoreMenu> findByStore_StoreId(String storeId) {
		return storeMenuRepository.findByStore_StoreId(storeId);
	}
	
	public void save(StoreMenu storeMenu) {
        storeMenuRepository.save(storeMenu);
    }
	

    @Override
    public StoreMenu updateStoreMenu(String menuId, String foodFirst, String foodMain, String foodDessert) {
        StoreMenu existingMenu = storeMenuRepository.findById(menuId).orElse(null);

        if (existingMenu == null) {
            throw new IllegalArgumentException("Menu không tồn tại");
        }

        existingMenu.setFoodFirst(foodFirst);
        existingMenu.setFoodMain(foodMain);
        existingMenu.setFoodDessert(foodDessert);

        return storeMenuRepository.save(existingMenu);
    }

    @Override
    public StoreMenu deleteStoreMenu(String menuId) {
        StoreMenu menu = storeMenuRepository.findById(menuId).orElse(null);

        if (menu == null) {
            throw new IllegalArgumentException("Menu không tồn tại");
        }

        storeMenuRepository.delete(menu);
        return menu; // Trả về menu đã xóa để lấy storeId
    }

	@Override
	public Optional<StoreMenu> findById(String menuId) {
		return storeMenuRepository.findById(menuId);
	}
}