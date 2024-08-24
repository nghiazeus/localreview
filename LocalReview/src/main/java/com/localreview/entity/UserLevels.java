package com.localreview.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.localreview.entityEnum.UserLevelEnum;

import lombok.Data;

@Entity
@Data
@Table(name = "UserLevels")
public class UserLevels {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private String userId;

    @Column(name = "points")
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private UserLevelEnum level;

    // Getters and Setters
}
