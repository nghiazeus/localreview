package com.localreview.entity;

import lombok.Data;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name = "Stores")
@Data
public class Store {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_id", columnDefinition = "CHAR(36)")
    private String storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_size", nullable = false)
    private String storeSize;

    @Column(name = "address_city", nullable = false)
    private String addressCity;

    @Column(name = "address_district", nullable = false)
    private String addressDistrict;

    @Column(name = "address_commune", nullable = false)
    private String addressCommune;

    @Column(name = "address_street", nullable = false)
    private String addressStreet;

    @Column(name = "owner_id", nullable = false, columnDefinition = "CHAR(36)")
    private String ownerId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private java.sql.Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private java.sql.Timestamp updatedAt;
}