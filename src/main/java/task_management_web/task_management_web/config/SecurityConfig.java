package task_management_web.task_management_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login").permitAll() // Các trang công khai
                        .requestMatchers("/admin/**").hasRole("Admin") // Chỉ cho phép Admin truy cập
                        .requestMatchers("/teamleader/**").hasAnyRole("TeamLeader", "Admin") // TeamLeader và Admin truy cập
                        .requestMatchers("/staff/**").hasAnyRole("Staff", "TeamLeader", "Admin") // Staff, TeamLeader và Admin truy cập
                        .anyRequest().authenticated() // Các request khác yêu cầu xác thực
                )
                .formLogin(form -> form
                        .loginPage("/auth/login") // Trang login tùy chỉnh
                        .loginProcessingUrl("/auth/login")
                        .permitAll() // Cho phép tất cả mọi người truy cập trang login
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler()) // Xử lý khi người dùng không có quyền
                );

        return http.build();
    }
    // Tạo PasswordEncoder cho việc mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Tạo AccessDeniedHandler để xử lý các lỗi truy cập trái phép (403)
    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
