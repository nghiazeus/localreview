package com.localreview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.entity.UserFavorites;

public interface UserFavoritesService {
	
	void save(UserFavorites favorite);
	void delete(UserFavorites favorite);
	boolean existsByUserAndStore(User user, Store store);
	UserFavorites findByUserAndStore(User user, Store store);
	List<UserFavorites> getFavoriteStoresByUserId(String userId);

}
