package com.localreview.entity;

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
import lombok.Data;

@Entity
@Data
@Table(name = "QRCodeScans")
public class QRCodeScans {

    @Id
    @Column(name = "qr_id", columnDefinition = "CHAR(36)")
    private String qrId = UUID.randomUUID().toString(); // Tạo UUID tự động


    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "scan_id", nullable = false, unique = true)
    private String scanId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @Column(name = "is_reviewed")
    private Boolean isReviewed;

    @Column(name = "qr_code_url")
    private String qrCodeUrl; // Thêm trường này để lưu URL của mã QR

    // Getters và Setters sẽ được sinh tự động bởi @Data từ Lombok
}
