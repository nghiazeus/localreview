package com.localreview.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.localreview.entity.CustomUserDetails;
import com.localreview.entity.Notification;
import com.localreview.service.ReviewService;
import com.localreview.serviceiml.NotificationService;

@Controller
@SessionAttributes("userId")
public class NotificationController {
	
	@Autowired
    private NotificationService notificationService ;

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/notifications")
    public String getNotificationsPage(Model model) {
    	// Lấy thông tin người dùng từ Principal (authentication)
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    Object principal = authentication.getPrincipal();

	    String userId = null;

	    if (principal instanceof CustomUserDetails) {
	        // Nếu là CustomUserDetails, lấy userId từ CustomUserDetails
	        CustomUserDetails userDetails = (CustomUserDetails) principal;
	        userId = userDetails.getUserId();
	        System.out.println("User ID retrieved from CustomUserDetails in index: " + userId);
	    } else if (principal instanceof DefaultOAuth2User) {
	        // Nếu là OAuth2User (Google OAuth2), lấy userId từ attributes của DefaultOAuth2User
	        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) principal;
	        
	        // Lấy userId từ attributes (đã được thêm vào trong CustomOAuth2UserService)
	        userId = oAuth2User.getAttribute("userId");
	        System.out.println("User ID retrieved from Google OAuth2User in index: " + userId);
	    }

	    if (userId != null) {
	        model.addAttribute("userId", userId);
	    } else {
	        System.out.println("User details are not available. Authentication may not be valid.");
	    }
        
        return "notifications"; // Trả về tên của file notifications.html
    }

    @MessageMapping("/sendNotification")
    public void sendNotification(String userId, String message) {
        // Gửi thông báo đến userB (người nhận thông báo)
        messagingTemplate.convertAndSendToUser(userId, "/topic/notifications", message);
    }
    
 // Phương thức để lấy thông báo cho người dùng
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        // Giả sử bạn có phương thức trong service để tìm thông báo theo ID
        Notification notification = notificationService.getNotificationById(id);
        
        if (notification != null) {
            notification.setRead(true);// Cập nhật trạng thái
            notificationService.save(notification); // Lưu thông báo đã cập nhật
            return ResponseEntity.ok().build(); // Trả về 200 OK
        }
        
        return ResponseEntity.notFound().build(); // Nếu không tìm thấy thông báo
    }


}
