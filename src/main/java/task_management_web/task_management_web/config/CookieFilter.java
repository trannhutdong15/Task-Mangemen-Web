package task_management_web.task_management_web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import task_management_web.task_management_web.service.SessionTokenService;

import java.io.IOException;
import java.util.Collections;

@Component
public class CookieFilter extends OncePerRequestFilter {

    @Autowired
    private SessionTokenService sessionTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {
        String token = null;

        // Retrieve the session token from the SESSIONID cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SESSIONID".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Validate the token and set authentication if valid
        if (token != null && sessionTokenService.validateToken(token)) {
            Integer userId = sessionTokenService.getUserIdFromToken(token); // Lấy userId từ token
            String username = sessionTokenService.getUsernameFromToken(token);
            String role = sessionTokenService.getRoleFromToken(token);
            String workAreaId = sessionTokenService.getWorkAreaIdFromToken(token);

            // Tạo danh sách quyền hạn với role
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the security context with the authenticated user
            SecurityContextHolder.getContext().setAuthentication(authToken);

            if (userId != null) {
                request.getSession().setAttribute("userId", userId); // Lưu userId vào session
            }
            if (workAreaId != null) {
                request.getSession().setAttribute("workAreaId", workAreaId); // Lưu workAreaId vào session
            }
        }

        chain.doFilter(request, response);
    }
}

