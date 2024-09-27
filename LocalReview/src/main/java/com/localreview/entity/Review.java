package com.localreview.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@Table(name = "Reviews")
public class Review {

	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id", columnDefinition = "CHAR(36)")
    private String reviewId;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnore
    private Store store;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comment")
    private String comment;
    
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Photo> photos;
    
    @Column(name = "review_date", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date reviewDate;

    public String getRelativeReviewDate() {
        if (reviewDate == null) {
            return "Không xác định"; // Trả về giá trị mặc định nếu reviewDate là null
        }

        LocalDate now = LocalDate.now();
        LocalDate reviewLocalDate = reviewDate.toLocalDate(); // Chuyển đổi java.sql.Date sang LocalDate
        long daysBetween = ChronoUnit.DAYS.between(reviewLocalDate, now);

        if (daysBetween == 0) {
            return "Hôm nay";
        } else if (daysBetween == 1) {
            return "Hôm qua";
        } else if (daysBetween <= 6) {
            return daysBetween + " ngày trước";
        } else if (daysBetween <= 13) {
            return "1 tuần trước";
        } else if (daysBetween <= 30) {
            return daysBetween / 7 + " tuần trước";
        } else if (daysBetween <= 365) {
            return daysBetween / 30 + " tháng trước";
        } else {
            long yearsBetween = ChronoUnit.YEARS.between(reviewLocalDate, now);
            return yearsBetween + (yearsBetween == 1 ? " năm trước" : " năm trước");
        }
    }

}