package com.localreview.serviceiml;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.service.StoreMenuService;

import java.util.List;

@Service
public class StoreMenuServiceImpl implements StoreMenuService {

	@Autowired
    private StoreMenuRepository menuRepository;

	@Override
    public List<StoreMenu> getMenuByStoreId(String storeId) {
        // Trả về danh sách thực đơn dựa trên storeId
        return menuRepository.findByStore_StoreId(storeId);
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