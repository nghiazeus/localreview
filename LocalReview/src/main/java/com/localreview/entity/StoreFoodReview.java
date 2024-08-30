package com.localreview.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Store_Food_Review")
@Data
public class StoreFoodReview {

    @Id
    @Column(name = "review_id", columnDefinition = "CHAR(36)")
    private String reviewId = UUID.randomUUID().toString(); // Tạo UUID tự động

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, columnDefinition = "CHAR(36)")
    private Store store; // Liên kết tới Store

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false, columnDefinition = "CHAR(36)")
    private StoreFood storeFood; // Liên kết tới StoreFood
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drink_id", nullable = false, columnDefinition = "CHAR(36)")
    private StoreFood storeDrink; // Liên kết tới StoreFood

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private User user; // Liên kết tới User

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    // Getters and Setters có thể được sinh ra bởi @Data của Lombok
}
