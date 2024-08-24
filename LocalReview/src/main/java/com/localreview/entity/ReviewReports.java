package com.localreview.entity;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "ReviewReports")
public class ReviewReports {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "report_id", columnDefinition = "CHAR(36)")
    private String reportId;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private com.localreview.entityEnum.ReportStatus status;

    // Getters and Setters
}