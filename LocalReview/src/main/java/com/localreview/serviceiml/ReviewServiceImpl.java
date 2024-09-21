package com.localreview.serviceiml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.localreview.DTO.ReviewWithPhotosDTO;
import com.localreview.entity.Photo;
import com.localreview.entity.Review;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.PhotoRepository;
import com.localreview.repository.ReviewRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;
import com.localreview.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Value("${imgur.client.id}")
    private String IMGUR_CLIENT_ID;

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";
	
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PhotoRepository photoRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StoreRepository storeRepository; // Thêm repository Store

    @Override
    public Review saveReview(Review review) {
        // Logic để lưu review
        return reviewRepository.save(review);
    }
    
    @Override
    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }
    
    @Override
    public Store getStoreById(String storeId) {
        return storeRepository.findById(storeId).orElse(null);
    }

    @Override
    public List<Review> getReviewsByStore(String storeId) {
        
        return reviewRepository.findReviewsWithPhotosByStoreId(storeId);
    }
    
    
 // Phương thức để lấy User từ tên người dùng
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String uploadImageToImgur(String imagePath) {
        File file = new File(imagePath);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(IMGUR_UPLOAD_URL);
            uploadFile.setHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("image", file, ContentType.DEFAULT_BINARY, file.getName());

            uploadFile.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseString);
                return jsonObject.getJSONObject("data").getString("link");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploading to Imgur", e);
        }
    }

	@Override
    public List<ReviewWithPhotosDTO> getReviewsWithPhotosByStoreId(String storeId) {
        // Lấy danh sách các đánh giá của cửa hàng từ repository
        List<Review> reviews = reviewRepository.findByStore_StoreId(storeId);

        // Tạo DTO từ các đánh giá và ảnh
        return reviews.stream().map(review -> {
            // Lấy các ảnh tương ứng với review_id
            List<String> photoUrls = photoRepository.findByReviewId(review.getReviewId())
                    .stream().map(Photo::getPhotoUrl)
                    .collect(Collectors.toList());

            // Tạo DTO
            return new ReviewWithPhotosDTO(
                    review.getReviewId(),
                    review.getUser().getUserId(),
                    review.getUser().getName(),
                    review.getComment(),
                    review.getRating(),
                    review.getReviewDate().toString(),
                    photoUrls
            );
        }).collect(Collectors.toList());
    }
}