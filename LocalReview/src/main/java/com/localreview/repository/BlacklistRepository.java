package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Blacklist;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {
    
}