package com.localreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
	List<Store> findByOwnerId(String ownerId);

    
}
