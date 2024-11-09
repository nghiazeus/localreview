package com.localreview.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "Notifications")
public class Notification {

    @Id
    @Column(name = "notification_id", columnDefinition = "CHAR(36)")
    private String notificationId = UUID.randomUUID().toString(); // Tạo UUID tự động

    @Column(name = "user_id", nullable = false)
    private String userId; // Người nhận thông báo

    @Column(name = "store_id")
    private String storeId; // Cửa hàng liên quan (nếu có)

    @Column(name = "review_id")
    private String reviewId; // Đánh giá liên quan (nếu có)

    @Column(name = "payment_id")
    private String paymentId; // Thanh toán liên quan (nếu có)

    @Column(name = "report_id")
    private String reportId; // Báo cáo liên quan (nếu có)

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "ENUM('new_review', 'review_report', 'payment_status', 'level_up')")
    private NotificationType type; // Loại thông báo (ENUM)

    @Column(name = "message", nullable = false)
    private String message; // Nội dung thông báo

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // Đánh dấu thông báo đã đọc

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Thời gian tạo thông báo

    // Thêm constructor để tự động thiết lập createdAt
    public Notification() {
        this.createdAt = LocalDateTime.now(); // Gán thời gian tạo khi khởi tạo đối tượng
    }

    // Enum cho loại thông báo
    public enum NotificationType {
        new_review,
        REVIEW_REPORT,
        PAYMENT_STATUS,
        LEVEL_UP
    }
}
