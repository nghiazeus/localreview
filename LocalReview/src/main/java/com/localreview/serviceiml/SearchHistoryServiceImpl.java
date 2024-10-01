package com.localreview.serviceiml;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.SearchHistory;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.SearchHistoryRepository;
import com.localreview.service.SearchHistoryService;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

	@Autowired
	private SearchHistoryRepository searchHistoryRepository;


	@Override
	public List<SearchHistory> getSearchHistoryByUser(User user) {
		return searchHistoryRepository.findByUserOrderBySearchDateDesc(user);
	}


	@Override
	public void saveSearchHistory(SearchHistory searchHistory) {
	    // Kiểm tra xem từ khóa đã tồn tại trong lịch sử tìm kiếm của người dùng chưa
	    boolean exists = searchHistoryRepository.existsByUserAndSearchTerm(searchHistory.getUser(), searchHistory.getSearchTerm());
	    if (!exists) {
	        searchHistoryRepository.save(searchHistory);
	    }
	}



	@Override
	public void deleteSearchHistoryById(String historyId) {
	    searchHistoryRepository.deleteById(historyId); 
	}

}
