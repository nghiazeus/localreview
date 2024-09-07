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
@Table(name = "Store_Menu")
@Data
public class StoreMenu {

    @Id
    @Column(name = "menu_id", columnDefinition = "CHAR(36)")
    private String menuId = UUID.randomUUID().toString(); // Tạo UUID tự động

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, columnDefinition = "CHAR(36)")
    private Store store; // Liên kết tới Store

    @Column(name = "food_first")
    private String foodFirst;

    @Column(name = "food_main")
    private String foodMain;

    @Column(name = "food_dessert")
    private String foodDessert;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    // Getters and Setters có thể được sinh ra bởi @Data của Lombok
}