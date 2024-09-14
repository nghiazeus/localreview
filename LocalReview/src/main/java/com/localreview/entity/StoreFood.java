package com.localreview.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Store_Food")
@Data
public class StoreFood {

    @Id
    @Column(name = "food_id", columnDefinition = "CHAR(36)")
    private String foodId = UUID.randomUUID().toString(); // Tạo UUID tự động

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, columnDefinition = "CHAR(36)")
    private Store store; // Liên kết tới Store
    
    @OneToMany(mappedBy = "food", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Photo> photos;
    
    @OneToMany(mappedBy = "storeFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreFoodReview> reviews;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(name = "price", nullable = false)
    private BigDecimal price; // Sử dụng Double cho giá

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    // Getters and Setters có thể được sinh ra bởi @Data của Lombok
}
