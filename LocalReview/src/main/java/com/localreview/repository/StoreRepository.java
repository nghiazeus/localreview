package com.localreview.repository;

import java.awt.print.Pageable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Review;
import com.localreview.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
	
//	@Query("SELECT s FROM Store s ORDER BY FUNCTION('RAND')")
//    List<Store> findRandomStores();
	
	List<Store> findByOwnerId(String ownerId);
	
	@Query("SELECT s FROM Store s WHERE LOWER(s.storeName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Store> searchStoresByName(@Param("searchTerm") String searchTerm);
	
	List<Store> findByStoreCategories_CategoriesId(String categoriesId);
	
	@Query("SELECT s FROM Store s JOIN s.reviews r GROUP BY s.id ORDER BY AVG(r.rating) DESC")
    List<Store> findTopRatedStores();

	List<Store> findByStoreNameContainingIgnoreCase(String storeName);
	
	@Query("SELECT s FROM Store s ORDER BY s.favoriteCount DESC")
    List<Store> findTop3ByOrderByFavoriteCountDesc(PageRequest pageRequest);
	
}
