package com.localreview.serviceiml;

import java.util.List;

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

}
