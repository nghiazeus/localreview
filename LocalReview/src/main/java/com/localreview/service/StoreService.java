package com.localreview.service;

import java.util.List;
import java.util.Optional;

import com.localreview.entity.Store;

public interface StoreService {
	
	List<Store> getAllStores();

	void saveStore(Store store);
	
	Store findStoreById(String id);
	
// Store of user
    
    List<Store> getStoresByOwnerId(String ownerId);
    void updateStore(Store store);
    public void deleteStore(String id);

}
