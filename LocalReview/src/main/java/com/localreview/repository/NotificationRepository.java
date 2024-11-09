package com.localreview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Blacklist;
import com.localreview.entity.Notification;
import com.localreview.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
	
	List<Notification> findByUserId(String userId);
}
