package com.localreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.localreview.entity.QRCodeScans;

@Repository
public interface QRCodeScanRepository extends JpaRepository<QRCodeScans, String> {
    
}
