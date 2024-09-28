package com.localreview.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ReviewPoints")
public class ReviewPoints {
    @Id
    @GeneratedValue
    private UUID pointId;

    private String userId; // ID của người dùng
    private int points; // Số điểm
    private String storeId; // ID của cửa hàng

    // Các phương thức khác nếu cần thiết
}
