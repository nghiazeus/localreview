package com.localreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.localreview.entity.Review;
import com.localreview.entity.ReviewReports;

@Repository 
public interface ReviewReportRepository extends JpaRepository<ReviewReports, String> {
	
}