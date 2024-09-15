package com.localreview.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.localreview.entity.Photo;
import com.localreview.entity.Store;

public interface StoreService {

	List<Store> getAllStores();

	Store saveStore(Store store);

	Store findStoreById(String id);
	
	Optional<Store> getStoreById(String storeId);

// Store of user
	List<Store> getStoresByOwnerId(String ownerId);

	Store updateStore(Store store);

	void deleteStore(String storeId);

//    Random store
	List<Store> getRandomStores();

//    Search...
	List<Store> searchStores(String searchTerm);

	String uploadImageToImgur(String imagePath);

	Photo savePhoto(Photo photo);

//	Lọc của hàng theo danh mục
	List<Store> getStoresByCategoryId(String categoriesId);

}
