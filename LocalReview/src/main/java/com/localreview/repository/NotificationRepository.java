package com.localreview.repository;

import com.localreview.entity.Notification;
import com.localreview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserAndIsReadFalse(User user);
}
