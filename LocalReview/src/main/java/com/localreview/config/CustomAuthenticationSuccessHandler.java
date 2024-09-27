package com.localreview.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Kiểm tra quyền của người dùng
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"));
        
        if (isAdmin) {
            // Nếu người dùng là admin, chuyển hướng đến trang admin
            response.sendRedirect("/admin/dashboard");
        } else {
            // Nếu không, chuyển hướng đến trang mặc định
            response.sendRedirect("/index");
        }
    }
}
