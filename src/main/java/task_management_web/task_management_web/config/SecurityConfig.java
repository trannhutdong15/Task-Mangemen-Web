package task_management_web.task_management_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import task_management_web.task_management_web.exception.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, CookieFilter cookieFilter) throws Exception {

            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login", "/register", "/login_validate", "/register_validate").permitAll()
                            .requestMatchers("/css/**", "/js/**", "/plugin/images/**").permitAll()
                            .requestMatchers("/home").hasAnyAuthority("Staff", "TeamLeader")
                            .requestMatchers("/tasks/update/**", "/tasks/delete/**" , "/tasks/create-task" , "/tasks/staff/**" , "/tasks/details/**").hasAuthority("TeamLeader")
                            .requestMatchers("/tasks/details/**" , "/tasks/dashboard").hasAnyAuthority("Staff", "TeamLeader")
                            .requestMatchers("/admin/**").hasAuthority("Admin")
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .permitAll()
                    )
                    .exceptionHandling(exception -> exception
                            .accessDeniedHandler(new CustomAccessDeniedHandler())
                    )
                    .addFilterBefore(cookieFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

}
