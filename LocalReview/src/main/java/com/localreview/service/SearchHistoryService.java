package com.localreview.service;

import java.util.List;

import com.localreview.entity.SearchHistory;
import com.localreview.entity.Store;
import com.localreview.entity.User;

public interface SearchHistoryService {
	
	List<SearchHistory> getSearchHistoryByUser(User user);

	void saveSearchHistory(SearchHistory searchHistory);
	
	void deleteSearchHistoryById(String historyId);

}
