package com.localreview.service;

import java.util.List;

import com.localreview.entity.Store;

public interface StoreService {
	
	List<Store> getAllStores();

	void saveStore(Store store);
	
	Store findStoreById(String id);

}
