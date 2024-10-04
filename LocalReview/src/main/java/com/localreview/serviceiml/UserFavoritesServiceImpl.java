package com.localreview.serviceiml;

import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.entity.UserFavorites;
import com.localreview.repository.UserFavoritesRepository; // Giả định có repository để truy cập dữ liệu
import com.localreview.repository.UserRepository; // Giả định có repository để truy cập dữ liệu
import com.localreview.service.StoreService;
import com.localreview.service.UserFavoritesService;
import com.localreview.service.UserService;
import com.localreview.repository.StoreRepository; // Giả định có repository để truy cập dữ liệu

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserFavoritesServiceImpl implements UserFavoritesService {
	@Autowired
    private UserFavoritesRepository userFavoritesRepository;
	
	@Autowired
    private StoreService storeService;


	public void save(UserFavorites userFavorite) {
        userFavoritesRepository.save(userFavorite);
        // Tăng số lượng yêu thích cho cửa hàng
        Store store = userFavorite.getStore();
        store.incrementFavoriteCount();
        storeService.saveStore(store); // Lưu cập nhật vào cơ sở dữ liệu
    }

    public void delete(UserFavorites userFavorite) {
        userFavoritesRepository.delete(userFavorite);
        // Giảm số lượng yêu thích cho cửa hàng
        Store store = userFavorite.getStore();
        store.decrementFavoriteCount();
        storeService.saveStore(store); // Lưu cập nhật vào cơ sở dữ liệu
    }

	@Override
	public boolean existsByUserAndStore(User user, Store store) {
		 return userFavoritesRepository.existsByUserAndStore(user, store);
	}

	@Override
	public UserFavorites findByUserAndStore(User user, Store store) {
		return userFavoritesRepository.findByUserAndStore(user, store);
	}

	@Override
	public List<UserFavorites> getFavoriteStoresByUserId(String userId) {
		return userFavoritesRepository.findByUser_UserId(userId);
	}


}
