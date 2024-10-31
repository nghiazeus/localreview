package com.localreview.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Entity
@Data
@Table(name = "Blacklist")
public class Blacklist {

	@Id
	 @GeneratedValue(generator = "UUID")
	    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	    @Column(name = "id", columnDefinition = "CHAR(36)")
	    private String id;

	    @ManyToOne
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user; // Liên kết với bảng Users

	    @Column(name = "reason", nullable = false)
	    private String reason; // Lý do bị blacklist

	    @Column(name = "created_at", nullable = false)
	    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Hoặc định dạng bạn muốn
	    private LocalDateTime createdAt; // Thời gian tạo

	    @Column(name = "expires_at", nullable = false)
	    private LocalDateTime expiresAt; // Thời gian hết hạn
	    
	    @Column(name = "is_active") // Thêm annotation @Column nếu cần
	    private boolean isActive = true; // Đặt giá trị mặc định là true
    // Getters and Setters
}
