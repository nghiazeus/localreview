package com.localreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    
}
