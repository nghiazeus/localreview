package com.localreview.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.localreview.DTO.ReviewReportDTO;
import com.localreview.entity.ReviewReports;
import com.localreview.entityEnum.ReportStatus;

public interface ReviewReportService {
	
	public ReviewReports reportReview(ReviewReportDTO reportDTO);
	    
	 Optional<ReviewReports> getReportById(String reportId);
	    
	 List<ReviewReports> getAllReports();
	    
	 
}
