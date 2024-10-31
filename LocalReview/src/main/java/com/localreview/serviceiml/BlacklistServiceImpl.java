package com.localreview.serviceiml;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.localreview.entity.Blacklist;
import com.localreview.repository.BlacklistRepository;
import com.localreview.service.BlacklistService;

@Service
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistRepository blacklistRepository;

    @Autowired
    public BlacklistServiceImpl(BlacklistRepository blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

    @Override
    public List<Blacklist> getAllBlacklists() {
        return blacklistRepository.findAll();
    }

    @Override
    public Optional<Blacklist> getBlacklistById(String id) {
        return blacklistRepository.findById(id);
    }

    @Override
    public Blacklist addUserToBlacklist(Blacklist blacklist) {
        return blacklistRepository.save(blacklist);
    }

    @Override
    public void deleteBlacklistById(String id) {
        blacklistRepository.deleteById(id);
    }

    @Override
    public boolean isUserBlacklisted(String userId) {
        // Bạn có thể thay đổi phương thức này nếu cần kiểm tra thêm logic
        return blacklistRepository.existsById(userId);
    }
}
