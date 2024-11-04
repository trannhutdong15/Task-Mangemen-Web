package task_management_web.task_management_web.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionTokenService {
    private static class SessionInfo {
        private final String username;
        private final String role;

        public SessionInfo(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }

    private final Map<String, SessionInfo> sessionStore = new HashMap<>(); // Temporary in-memory storage

    // Generate a session token and store it with the associated username and role
    public String createSessionToken(String username, String role) {
        String token = UUID.randomUUID().toString(); // Generate a unique token
        sessionStore.put(token, new SessionInfo(username, role)); // Store username and role with token
        return token;
    }

    // Validate the session token
    public boolean validateToken(String token) {
        return sessionStore.containsKey(token);
    }

    // Retrieve the username associated with a token
    public String getUsernameFromToken(String token) {
        SessionInfo sessionInfo = sessionStore.get(token);
        return sessionInfo != null ? sessionInfo.getUsername() : null;
    }

    // Retrieve the role associated with a token
    public String getRoleFromToken(String token) {
        SessionInfo sessionInfo = sessionStore.get(token);
        return sessionInfo != null ? sessionInfo.getRole() : null;
    }

    // Remove a session token (useful for logout functionality)
    public void deleteSession(String token) {
        sessionStore.remove(token);
    }
}

