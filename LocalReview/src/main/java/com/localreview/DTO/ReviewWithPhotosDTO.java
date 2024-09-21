package com.localreview.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewWithPhotosDTO {
    private String reviewId;
    private String userId;
    private String userName;
    private String comment;
    private int rating;
    private String reviewDate;
    private List<String> photoUrls;
}
