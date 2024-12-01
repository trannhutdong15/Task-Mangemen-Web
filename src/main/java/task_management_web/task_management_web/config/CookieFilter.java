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
            // Get all necessary information from the session token
            Integer userId = sessionTokenService.getUserIdFromToken(token); // Get userId from token
            String username = sessionTokenService.getUsernameFromToken(token);
            String role = sessionTokenService.getRoleFromToken(token);
            String workAreaId = sessionTokenService.getWorkAreaIdFromToken(token);

            // Create the authorities based on the user's role
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the security context with the authenticated user
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Store all session data into HttpSession
            if (userId != null) {
                request.getSession().setAttribute("userId", userId); // Store userId in session
            }
            if (username != null) {
                request.getSession().setAttribute("username", username); // Store username in session
            }
            if (role != null) {
                request.getSession().setAttribute("role", role); // Store role in session
            }
            if (workAreaId != null) {
                request.getSession().setAttribute("workAreaId", workAreaId); // Store workAreaId in session
            }
        }

        chain.doFilter(request, response); // Continue processing the request
    }
}
