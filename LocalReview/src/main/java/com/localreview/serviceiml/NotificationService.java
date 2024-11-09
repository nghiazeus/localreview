package com.localreview.serviceiml;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.localreview.entity.Notification;
import com.localreview.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository; // Thêm vào đây

    // Phương thức gửi thông báo thời gian thực và lưu vào database
    public void sendRealTimeNotification(String message, String type, String userId) {
        // Tạo đối tượng Notification
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(Notification.NotificationType.valueOf(type)); // Đảm bảo bạn chuyển đổi đúng kiểu ENUM
        notification.setUserId(userId); // Gán userId của người nhận thông báo
        notification.setRead(false); // Mặc định chưa đọc

        // Lưu thông báo vào cơ sở dữ liệu
        notificationRepository.save(notification); // Lưu thông báo vào cơ sở dữ liệu

        // Gửi thông báo đến kênh theo userId
        messagingTemplate.convertAndSendToUser(userId, "/topic/notifications", notification);

    }
    
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }
    
   
    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id).orElse(null); // Tìm thông báo theo ID
    }
    
    public void save(Notification notification) {
        notificationRepository.save(notification); // Lưu thông báo
    }
}