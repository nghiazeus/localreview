package com.localreview.repository;

import com.localreview.entity.SearchHistory;
import com.localreview.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, String> {
	
	boolean existsByUserAndSearchTerm(User user, String searchTerm);
	
	List<SearchHistory> findByUserOrderBySearchDateDesc(User user);
	
	
}
