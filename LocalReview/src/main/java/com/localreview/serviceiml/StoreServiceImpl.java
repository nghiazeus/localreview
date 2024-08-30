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
	StoreRepository storere;

	@Override
	public List<Store> getAllStores() {
		return storere.findAll();
	}

	@Override
    public void saveStore(Store store) {
        storere.save(store);
    }

}
