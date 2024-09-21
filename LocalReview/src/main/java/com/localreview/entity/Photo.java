package com.localreview.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "Photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "photo_id", updatable = false, nullable = false, length = 36)
    private String photoId;

    @Column(name = "store_id", length = 36)
    private String storeId;

    @Column(name = "menu_id", length = 36)
    private String menuId;

    @Column(name = "food_id", length = 36)
    private String foodId;

    @Column(name = "drink_id", length = 36)
    private String drinkId;

    @Column(name = "review_id", length = 36)
    private String reviewId;

    @Column(name = "food_review_id", length = 36)
    private String foodReviewId;
    
    @Column(name = "photo_type", length = 36)
    private String phototype;

    @Column(name = "photo_url", nullable = false, length = 255)
    private String photoUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "uploaded_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime uploadedAt;

    // Để tạo mối quan hệ với các bảng khác (nếu cần thiết)
    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "menu_id", insertable = false, updatable = false)
    private StoreMenu menu;

    @ManyToOne
    @JoinColumn(name = "food_id", insertable = false, updatable = false)
    private StoreFood food;

    @ManyToOne
    @JoinColumn(name = "drink_id", insertable = false, updatable = false)
    private StoreDrink drink;

    @ManyToOne
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "food_review_id", insertable = false, updatable = false)
    private StoreFoodReview foodReview;
    
    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }
}
