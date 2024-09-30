package com.localreview.serviceiml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.localreview.entity.Photo;
import com.localreview.entity.Store;
import com.localreview.repository.PhotoRepository;
import com.localreview.repository.ReviewRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreService;

@Service
public class StoreServiceImpl implements StoreService {

	@Value("${imgur.client.id}")
	private String IMGUR_CLIENT_ID;

	private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	private PhotoRepository photoRepository;
	
	@Autowired
    private ReviewRepository reviewRepository;

	@Override
	public List<Store> getAllStores() {
		return storeRepository.findAll();
	}

	public Store findStoreById(String id) {
		return storeRepository.findById(id).orElse(null);
	}

	// Store of user

	// Lấy tất cả các cửa hàng của một người dùng (dựa trên ownerId)
	public List<Store> getStoresByOwnerId(String ownerId) {
		return storeRepository.findByOwnerId(ownerId);
	}

	@Override
    public Store updateStore(Store store) {
        return storeRepository.save(store);
    }


	@Override
    public void deleteStore(String storeId) {
        storeRepository.deleteById(storeId);
    }

	@Override
	public List<Store> getRandomStores() {
		List<Store> stores = storeRepository.findAll(); // Lấy tất cả cửa hàng
		Collections.shuffle(stores); // Xáo trộn ngẫu nhiên danh sách
		return stores;
	}

	@Override
	public List<Store> searchStores(String searchTerm) {
		return storeRepository.searchStoresByName(searchTerm);
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
	public Photo savePhoto(Photo photo) {
		 return photoRepository.save(photo);
	}

	@Override
	public Store saveStore(Store store) {
		return storeRepository.save(store);
	}

	@Override
	public List<Store> getStoresByCategoryId(String categoriesId) {
		return storeRepository.findByStoreCategories_CategoriesId(categoriesId);
	}

	@Override
	public Optional<Store> getStoreById(String storeId) {
		return storeRepository.findById(storeId);
	}

	@Override
	public Double getAverageRating(String storeId) {
		Double averageRating = reviewRepository.findAverageRatingByStoreId(storeId);
        return (averageRating != null) ? averageRating : 0.0;
	}



}
