package com.localreview.serviceiml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.localreview.entity.CustomUserDetails; // Thêm dòng này

import com.localreview.entity.User;
import com.localreview.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Tạo đối tượng CustomUserDetails
        return new CustomUserDetails(
            user.getUserId(),      // ID người dùng
            user.getEmail(),   // Email
            user.getPassword(), // Mật khẩu
            getAuthorities(user), // Quyền hạn
            true,              // Tài khoản chưa hết hạn
            true,              // Tài khoản chưa bị khóa
            true,              // Chứng chỉ chưa hết hạn
            true               // Tài khoản đang hoạt động
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Chuyển đổi enum role thành quyền hợp lệ
        return Arrays.stream(user.getRole().name().split(","))
                     .map(SimpleGrantedAuthority::new)
                     .collect(Collectors.toList());
    }
}