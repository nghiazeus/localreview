package com.localreview.entity;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.localreview.entityEnum.ReportStatus;

import lombok.Data;

@Data
@Entity
@Table(name = "ReviewReports")
public class ReviewReports {

	 @Id
	 @GeneratedValue(generator = "UUID")
	    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	    @Column(name = "report_id", columnDefinition = "CHAR(36)")
	    private String reportId;
	    
	    @Column(name = "review_id", nullable = false)
	    private String reviewId;
	    
	    @ManyToOne  
	    @JoinColumn(name = "reported_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
	    private User reportedUserId; // ID của người dùng bị báo cáo
	    
	    @Column(name = "reported_user_id", nullable = false)
	    private String reportedUser;
	    
	    @ManyToOne
	    @JoinColumn(name = "reported_by", referencedColumnName = "user_id", insertable = false, updatable = false)
	    private User reportedBy;
	    
	    @Column(name = "reported_by", nullable = false)
	    private String reportedByuser;
	    
	    private boolean active = true; // Thêm thuộc tính active
	    
	    @Column(name = "reason", nullable = false)
	    private String reason;
	    
	    @Column(name = "reported_at", nullable = false)
	    private LocalDateTime reportedAt = LocalDateTime.now();
	    
	    @Enumerated(EnumType.STRING)
	    @Column(name = "status", nullable = false)
	    private ReportStatus status = ReportStatus.PENDING;
    // Getters và Setters
	    
}
