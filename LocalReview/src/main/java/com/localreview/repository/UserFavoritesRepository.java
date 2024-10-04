package com.localreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.entity.UserFavorites;

@Repository
public interface UserFavoritesRepository extends JpaRepository<UserFavorites, String> {
    
	boolean existsByUserAndStore(User user, Store store);
    UserFavorites findByUserAndStore(User user, Store store);
    List<UserFavorites> findByUser_UserId(String userId);
}

