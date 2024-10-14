package com.localreview.entity;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "Notifications")
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "notification_id", columnDefinition = "CHAR(36)")
    private String notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người nhận thông báo

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date(); // Thiết lập thời gian khi tạo thông báo
    }
}
