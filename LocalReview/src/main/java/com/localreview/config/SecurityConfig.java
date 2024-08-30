package com.localreview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.localreview.serviceiml.CustomOAuth2UserService;
import com.localreview.serviceiml.CustomUserDetailsService;
import com.localreview.serviceiml.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private UserDetailsService userDetailsService;
	
	@Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Vô hiệu hóa CSRF protection nếu cần thiết.
            .authorizeRequests()
                .antMatchers("/index","/login", "/register", "/css/**", "/js/**", "/images/**").permitAll() // Cho phép truy cập không cần xác thực cho các trang và tài nguyên cụ thể.
                .antMatchers("/user", "/register-store").hasAnyAuthority("user", "store_owner")
                .antMatchers("/admin/**").hasRole("admin")
                .anyRequest().authenticated() // Yêu cầu xác thực cho tất cả các yêu cầu khác.
            .and()
            .formLogin()
                .loginPage("/login") // Xác định trang đăng nhập tùy chỉnh.
                .defaultSuccessUrl("/index", true) // Chuyển hướng đến trang chính sau khi đăng nhập thành công.
                .failureUrl("/login?error=true") // URL chuyển hướng khi đăng nhập thất bại.
                .permitAll() // Cho phép tất cả người dùng truy cập trang đăng nhập.
            .and()
	            .oauth2Login()  // Bổ sung cấu hình cho OAuth2
	            .loginPage("/login")
	            .defaultSuccessUrl("/login/oauth2/success", true)
	            .failureUrl("/login?error=true")
	            .userInfoEndpoint()
	            .userService(customOAuth2UserService) // Sử dụng CustomOAuth2UserService để xử lý thông tin người dùng từ Google
	            .and()
	        .and()
            .logout()
                .logoutUrl("/logout") // URL để gửi yêu cầu đăng xuất.
                .logoutSuccessUrl("/login?logout=true") // URL chuyển hướng sau khi đăng xuất thành công.
                .permitAll() // Cho phép tất cả người dùng thực hiện đăng xuất.
            .and()
            .sessionManagement()
            .sessionFixation().newSession() // Tạo một session mới sau khi đăng nhập
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Luôn tạo một session mới
            .invalidSessionUrl("/login?invalidSession=true") // URL chuyển hướng khi session không hợp lệ
            .maximumSessions(1) // Giới hạn số lượng session cho mỗi người dùng
            .maxSessionsPreventsLogin(false) // Cho phép đăng nhập mới ngay cả khi đã đạt giới hạn session
            .expiredUrl("/login?expired=true"); // URL chuyển hướng khi session hết hạn
    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}