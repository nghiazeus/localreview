package com.localreview.service;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.QRCodeScans;
import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.repository.QRCodeScanRepository;


public interface QRCodeScansService {
    QRCodeScans createQRCodeScan(User user, Store store);
}
