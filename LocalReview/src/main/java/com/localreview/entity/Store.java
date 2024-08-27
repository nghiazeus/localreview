package com.localreview.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Stores")
@Data
public class Store {

	@Id
    @Column(name = "store_id", columnDefinition = "CHAR(36)")
    private String storeId = UUID.randomUUID().toString(); // Tạo UUID tự động

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_size", nullable = false)
    private String storeSize; // Thêm trường store_size để khớp với bảng SQL

    @Column(name = "address_city", nullable = false)
    private String addressCity; // Thêm trường address_city để khớp với bảng SQL

    @Column(name = "address_district", nullable = false)
    private String addressDistrict; // Thêm trường address_district để khớp với bảng SQL

    @Column(name = "address_commune", nullable = false)
    private String addressCommune; // Thêm trường address_Commune để khớp với bảng SQL

    @Column(name = "address_street", nullable = false)
    private String addressStreet; // Thêm trường address_Street để khớp với bảng SQL


    @Column(name = "owner_id", nullable = false, columnDefinition = "CHAR(36)")
    private String ownerId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.sql.Timestamp updatedAt;


    // Getters and Setters
}
