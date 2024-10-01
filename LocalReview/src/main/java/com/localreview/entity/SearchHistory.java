package com.localreview.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "SearchHistory")
public class SearchHistory {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "history_id", columnDefinition = "CHAR(36)")
    private String historyId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "search_term", nullable = false)
    private String searchTerm;

    @Column(name = "search_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date searchDate;

    @PrePersist
    protected void onCreate() {
        searchDate = new Date(); // Thiết lập thời gian khi tạo
    }
}

