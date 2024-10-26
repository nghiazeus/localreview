package com.localreview.serviceiml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.localreview.entity.Store;
import com.localreview.entity.User;
import com.localreview.entityEnum.UserRole;
import com.localreview.repository.StoreRepository;
import com.localreview.repository.UserRepository;
import com.localreview.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    
    

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mật khẩu trước khi lưu
        return userRepository.save(user);
    }
    

    public User findOrCreateUser(String email, String name, String googleId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setGoogleId(googleId);
            user.setRole(UserRole.user);
            userRepository.save(user);
        }
        return user;
    }

	@Override
	public User getUserById(String userId) {
		return userRepository.findById(userId).orElse(null);
	}

	
	@Override
	public User getUserByEmail(String email) {
	    return userRepository.findByEmail(email);
	}

	@Override
	public User findByGoogleId(String googleId) {
	    // Tìm người dùng theo googleId
	    User user = userRepository.findByGoogleId(googleId); // Trả về null nếu không tìm thấy
	    return user; // Trả về người dùng hoặc null
	}

    
}
