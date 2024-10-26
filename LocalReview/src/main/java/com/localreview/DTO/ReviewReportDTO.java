package com.localreview.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportDTO {
	  private String reviewId;
	  private String reportedUserId;
	  private String reportedBy;
	  private String reason;
}
