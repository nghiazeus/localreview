package com.localreview.service;

import java.util.List;
import java.util.Optional;

import com.localreview.entity.Blacklist;

public interface BlacklistService {
    // Lấy tất cả blacklist entries
    List<Blacklist> getAllBlacklists();

    // Lấy thông tin blacklist theo ID (ID là kiểu String)
    Optional<Blacklist> getBlacklistById(String id);

    // Thêm người dùng vào blacklist
    Blacklist addUserToBlacklist(Blacklist blacklist);

    // Xóa blacklist entry theo ID (ID là kiểu String)
    void deleteBlacklistById(String id);

    // Kiểm tra xem người dùng có trong blacklist không (userId là kiểu String)
    boolean isUserBlacklisted(String userId);
}
