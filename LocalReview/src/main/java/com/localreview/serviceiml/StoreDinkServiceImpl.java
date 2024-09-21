package com.localreview.serviceiml;

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

import com.localreview.entity.Photo;
import com.localreview.entity.Store;
import com.localreview.entity.StoreDrink;
import com.localreview.entity.StoreFood;
import com.localreview.entity.StoreMenu;
import com.localreview.repository.PhotoRepository;
import com.localreview.repository.StoreDrinkRepository;
import com.localreview.repository.StoreFoodRepository;
import com.localreview.repository.StoreMenuRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.StoreDrinkService;
import com.localreview.service.StoreFoodService;
import com.localreview.service.StoreMenuService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class StoreDinkServiceImpl implements StoreDrinkService {

	@Autowired
	private StoreDrinkRepository storeDrinkRepository;
	
	@Autowired
	private PhotoRepository photoRepository;
	
    @Value("${imgur.client.id}")
	private String IMGUR_CLIENT_ID;

	private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";


	@Override
	public List<StoreDrink> findByStore_StoreId(String storeId) {
		return storeDrinkRepository.findByStore_StoreId(storeId);
	}
	
	@Override
    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }

	@Override
	public StoreDrink updateStoreDrink(String drinkId, String drinkName, BigDecimal price) {
		StoreDrink existingMenu = storeDrinkRepository.findById(drinkId).orElse(null);

		if (existingMenu == null) {
			throw new IllegalArgumentException("Món ăn không tồn tại");
		}

		existingMenu.setDrinkName(drinkName);
		existingMenu.setPrice(price);

		return storeDrinkRepository.save(existingMenu);
	}

	@Override
	public StoreDrink deleteStoreDrink(String drinkId) {
		StoreDrink drink = storeDrinkRepository.findById(drinkId).orElse(null);

		if (drink == null) {
			throw new IllegalArgumentException("Món ăn không tồn tại");
		}
		storeDrinkRepository.delete(drink);
		return drink;
	}

	@Override
	public void save(StoreDrink newDrink) {
		storeDrinkRepository.save(newDrink);

	}

	@Override
	public Optional<StoreDrink> findById(String drinkId) {
		return storeDrinkRepository.findById(drinkId);
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

}