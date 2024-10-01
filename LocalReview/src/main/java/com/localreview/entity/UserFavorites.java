package com.localreview.entity;

import java.sql.Date;

import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "UserFavorites")
public class UserFavorites {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "favorite_id", updatable = false, nullable = false)
    private String favoriteId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "store_id", nullable = false)
    private String storeId;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

}
