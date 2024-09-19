package com.localreview.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Store;
import com.localreview.entity.StoreMenu;

@Repository
public interface StoreMenuRepository extends JpaRepository<StoreMenu, String> {
	
	List<StoreMenu> findByStore_StoreId(String storeId);

	
}



