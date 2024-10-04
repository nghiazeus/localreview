package com.localreview.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
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
    
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Vô hiệu hóa CSRF protection nếu cần thiết.
            .authorizeRequests()
                .antMatchers("/user", "/register-store").hasAnyAuthority("user", "store_owner")
                .antMatchers("/api/reviews").hasAuthority("store_owner")
                .antMatchers("/admin/dashboard").hasAuthority("admin") // Chỉ cho phép admin truy cập
                .antMatchers("/admin/**").hasAuthority("admin") // Chỉ cho phép admin truy cập vào tất cả các đường dẫn bắt đầu bằng /admin/
                .anyRequest().permitAll() // Cho phép tất cả các yêu cầu khác
            .and()
            .formLogin()
                .loginPage("/login") // Trang đăng nhập cho người dùng
                .successHandler(new CustomAuthenticationSuccessHandler()) // Sử dụng CustomAuthenticationSuccessHandler
                .failureUrl("/login?error=true") // Khi đăng nhập thất bại
                .permitAll()
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
                .logoutUrl("/logout") // URL để đăng xuất
                .logoutSuccessUrl("/login?logout=true") // Chuyển hướng sau khi đăng xuất thành công
                .permitAll()
            .and()
            .rememberMe()
                .tokenValiditySeconds(604800) // 86400 giây = 1 ngày
            .and()
            .sessionManagement()
                .sessionFixation().newSession() // Tạo session mới sau khi đăng nhập
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Luôn tạo session mới
                .invalidSessionUrl("/login?invalidSession=true") // URL chuyển hướng khi session không hợp lệ
                .maximumSessions(1) // Giới hạn số session
                .sessionRegistry(sessionRegistry()) // Sử dụng SessionRegistry
                .expiredUrl("/login?expired=true"); // URL khi session hết hạn
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