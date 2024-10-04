package com.localreview.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
import javax.persistence.Transient;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "Stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "store_id", columnDefinition = "CHAR(36)")
	private String storeId;

	@Column(name = "store_name", nullable = false)
	private String storeName;
	
	@Column(name = "view_count", nullable = false)
    private int viewCount = 0;  

	@Column(name = "favorite_count", nullable = false)
	private int favoriteCount = 0;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_categories", nullable = false)
	private Categories storeCategories;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Review> reviews;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<StoreMenu> storeMenus;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Photo> photos;

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
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Timestamp updatedAt;
	
	private int reviewCount;
	
	@Transient // Đảm bảo không lưu trong cơ sở dữ liệu
    private boolean favorited;

	public Double getAverageRating() {
		if (reviews == null || reviews.isEmpty()) {
			return 0.0;
		}
		return reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
	}
	
	public void incrementFavoriteCount() {
	    this.favoriteCount++;
	}

	public void decrementFavoriteCount() {
	    this.favoriteCount--;
	}
	
}
