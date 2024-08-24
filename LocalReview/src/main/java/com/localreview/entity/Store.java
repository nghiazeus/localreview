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

    @Column(name = "address", nullable = false)
    private String address;

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
