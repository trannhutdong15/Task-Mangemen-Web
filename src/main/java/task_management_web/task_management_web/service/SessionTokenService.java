package task_management_web.task_management_web.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionTokenService {

    @Getter
    private static class SessionInfo {
        private final Integer userId;
        private final String username;
        private final String role;
        private final String workAreaId;

        public SessionInfo(Integer userId,String username, String role, String workAreaId) {
            this.username = username;
            this.role = role;
            this.workAreaId = workAreaId;
            this.userId = userId;
        }

    }

    private final Map<String, SessionInfo> sessionStore = new ConcurrentHashMap<>(); // Thread-safe storage

    // Generate a session token and store it with the associated username, role, and workAreaId
    public String createSessionToken(Integer userId,String username, String role, String workAreaId) {
        String token = UUID.randomUUID().toString(); // Generate a unique token
        sessionStore.put(token, new SessionInfo(userId,username, role, workAreaId));
        return token;
    }

    // Validate the session token
    public boolean validateToken(String token) {
        return sessionStore.containsKey(token);
    }

    public Integer getUserIdFromToken(String token) {
        SessionInfo sessionInfo = sessionStore.get(token);
        return sessionInfo != null ? sessionInfo.getUserId() : null;
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

    // Retrieve the workAreaId associated with a token
    public String getWorkAreaIdFromToken(String token) {
        SessionInfo sessionInfo = sessionStore.get(token);
        return sessionInfo != null ? sessionInfo.getWorkAreaId() : "Not Assigned"; // Default to "Not Assigned" if null
    }

    // Remove a session token (useful for logout functionality)
    public void deleteSession(String token) {
        sessionStore.remove(token);
    }
}
