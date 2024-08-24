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

import lombok.Data;

@Entity
@Data
@Table(name = "ReviewHistory")
public class ReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "history_id", columnDefinition = "CHAR(36)")
    private String historyId;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "previous_rating")
    private Integer previousRating;

    @Column(name = "previous_comment")
    private String previousComment;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    // Getters and Setters
}