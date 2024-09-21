package com.localreview.serviceiml;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.localreview.entity.Photo;
import com.localreview.entity.Review;
import com.localreview.repository.PhotoRepository;
import com.localreview.repository.StoreRepository;
import com.localreview.service.PhotoService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoRepository photoRepository;
    
    @Autowired
    private StoreRepository storeRepository;

    @Value("${imgur.client.id}")
	private String IMGUR_CLIENT_ID;

	private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Override
    public Photo savePhoto(Photo photo) {
        return photoRepository.save(photo);
    }

    @Override
    public List<Photo> getPhotosByReviewId(String reviewId) {
        return photoRepository.findByReviewId(reviewId);
    }

//    @Override
//    public List<Photo> getPhotosByStoreId(String storeId) {
//        return photoRepository.findByStoreId(storeId);
//    }

    @Override
    public String uploadImageToImgur(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }

        File file = new File(imagePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + imagePath);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(IMGUR_UPLOAD_URL);
            uploadFile.setHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("image", file, ContentType.DEFAULT_BINARY, file.getName());
            uploadFile.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getBoolean("success")) {
                    return jsonObject.getJSONObject("data").getString("link");
                } else {
                    throw new RuntimeException("Failed to upload image: " + responseString);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading to Imgur", e);
        }
    }

    @Override
    public Path saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path tempFilePath = Paths.get(TEMP_DIR, fileName);

        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        return tempFilePath;
    }

	@Override
	public List<Photo> getPhotosByStoreId(String storeId) {
		return photoRepository.findByStoreIdAndPhotoType(storeId, "store");
	}





}

