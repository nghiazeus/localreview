package com.localreview.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String userId; // ID người dùng
    private String email; // Email của người dùng
    private String password; // Mật khẩu
    private Collection<? extends GrantedAuthority> authorities; // Quyền hạn
    private boolean isAccountNonExpired; // Tài khoản chưa hết hạn
    private boolean isAccountNonLocked; // Tài khoản chưa bị khóa
    private boolean isCredentialsNonExpired; // Chứng chỉ chưa hết hạn
    private boolean isEnabled; // Tài khoản đang hoạt động

    // Các phương thức của UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Trả về email thay vì tên đăng nhập
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
