package com.localreview.serviceiml;

import org.apache.http.HttpResponse;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.localreview.entity.QRCodeScans;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.QRCodeScanRepository;
import com.localreview.service.QRCodeScansService;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class QRCodeScansServiceImpl implements QRCodeScansService {

    @Autowired
    private QRCodeScanRepository qrCodeScansRepository;

    private final String IMGUR_CLIENT_ID = "95c3b2429c19504";
    private final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";

    @Override
    public QRCodeScans createQRCodeScan(User user, Store store) {
        QRCodeScans qrCodeScans = new QRCodeScans();
        qrCodeScans.setQrId(UUID.randomUUID().toString());
        qrCodeScans.setScanId(UUID.randomUUID().toString());
        qrCodeScans.setUser(user);
        qrCodeScans.setStore(store);
        qrCodeScans.setScannedAt(LocalDateTime.now());
        qrCodeScans.setIsReviewed(false);

        // Tạo mã QR với URL chứa mã storeId
        String storeId = store.getStoreId(); // Giả sử phương thức getStoreId() lấy mã storeId
        String reviewPageUrl = "http://192.168.1.102:8080/review?store_id=" + storeId;
        System.out.println("Generated URL: " + reviewPageUrl);
        
        String qrCodeImagePath = generateQRCodeImage(reviewPageUrl, 200, 200, "./QRCodeImages/" + qrCodeScans.getQrId() + ".png");
        System.out.println("QR Code image created at: " + qrCodeImagePath);
        
        // Tải ảnh lên Imgur và lưu URL trả về vào cơ sở dữ liệu
        String qrCodeUrl = uploadImageToImgur(qrCodeImagePath);
        qrCodeScans.setQrCodeUrl(qrCodeUrl);

        // Xóa tệp mã QR sau khi đã tải lên Imgur
        deleteQRCodeImage(qrCodeImagePath);

        return qrCodeScansRepository.save(qrCodeScans);
    }

    private String uploadImageToImgur(String imagePath) {
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
            return null;
        }
    }

    private static String generateQRCodeImage(String text, int width, int height, String filePath) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void deleteQRCodeImage(String filePath) {
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
