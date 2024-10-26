package com.localreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Blacklist;
import com.localreview.entity.User;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {
	 boolean existsByUserAndIsActiveTrue(User user); // Kiểm tra xem người dùng có trong blacklist không
	 
	 Optional<Blacklist> findByUserAndIsActiveTrue(User user);
	 
	 
}