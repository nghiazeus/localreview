package com.localreview.serviceiml;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreService;

@Service
public class StoreServiceImpl implements StoreService{
	
	@Autowired
	StoreRepository storeRepository;

	@Override
	public List<Store> getAllStores() {
		return storeRepository.findAll();
	}

	@Override
    public void saveStore(Store store) {
		storeRepository.save(store);
    }
	
	public Store findStoreById(String id) {
        return storeRepository.findById(id).orElse(null);
    }
	
	//Store of user
	
		// Lấy tất cả các cửa hàng của một người dùng (dựa trên ownerId)
	    public List<Store> getStoresByOwnerId(String ownerId) {
	        return storeRepository.findByOwnerId(ownerId);
	    }

	    public void updateStore(Store store) {
	        storeRepository.save(store);
	    }

	    public void deleteStore(String id) {
	        storeRepository.deleteById(id);
	    }

}
