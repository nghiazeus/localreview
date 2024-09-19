package com.localreview.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Store_Drink")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreDrink {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "drink_id", updatable = false, nullable = false, length = 36)
    private String drinkId;

    @Column(name = "store_id", nullable = false, length = 36)
    private String storeId;
    
    @OneToMany(mappedBy = "drink", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Photo> photos;
    
    @OneToMany(mappedBy = "storeDrink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreFoodReview> reviews;

    @Column(name = "drink_name", nullable = false, length = 255)
    private String drinkName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    // Để tạo mối quan hệ với bảng Stores (nếu cần)
    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;
    
    public String getFormattedPrice() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price) + " VND";
    }
}
