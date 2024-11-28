package task_management_web.task_management_web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.DTO.UserProfileDTO;
import task_management_web.task_management_web.service.SessionTokenService;
import task_management_web.task_management_web.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/api")
public class HomeController {

    private final UserService userService;
    private final SessionTokenService sessionTokenService;

    @Autowired
    public HomeController(UserService userService, SessionTokenService sessionTokenService) {
        this.userService = userService;
        this.sessionTokenService = sessionTokenService;
    }

    /**
     * Endpoint to serve the home page.
     *
     * @return the path to the `home.html` template
     */
    @GetMapping("/home")
    public String home() {
        return "user/home"; // Points to `templates/user/home.html`
    }

    /**
     * Retrieve the work area ID from the session.
     *
     * @param request the HTTP request containing the session
     * @return the work area ID if found, or a 401 Unauthorized response if not
     */
    @GetMapping("/workAreaId")
    @ResponseBody
    public ResponseEntity<String> getWorkAreaId(HttpServletRequest request) {
        String workAreaId = (String) request.getSession().getAttribute("workAreaId");

        if (workAreaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Work Area ID not found.");
        }

        return ResponseEntity.ok(workAreaId);
    }

    /**
     * Retrieve users by work area ID.
     *
     * @param workAreaId the work area ID
     * @return a list of `UserDTO` objects
     */
    @GetMapping("/users")
    @ResponseBody
    public List<UserDTO> getUsersByWorkAreaId(@RequestParam("workAreaId") String workAreaId) {
        return userService.getAllUsersByWorkAreaId(workAreaId);
    }

    /**
     * Retrieve the current user ID from the session or cookies.
     *
     * @param request the HTTP request containing the session and cookies
     * @return the current user ID
     */
    @GetMapping("/current-id")
    public ResponseEntity<Integer> getCurrentUserId(HttpServletRequest request) {
        try {
            Integer userId = userService.getUserIdFromSessionOrCookie(request);
            return ResponseEntity.ok(userId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Retrieve a user's profile by their user ID.
     *
     * @param userId the user ID
     * @return the `UserProfileDTO` object
     */
    @GetMapping("/profile")
    @ResponseBody
    public ResponseEntity<UserProfileDTO> getUserProfile(@RequestParam("userId") Integer userId) {
        try {
            UserProfileDTO userProfile = userService.getUserProfileById(userId);
            return ResponseEntity.ok(userProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Return 400 if an error occurs
        }
    }

    /**
     * Update a user's profile.
     *
     * @param userProfileDTO the updated profile data
     * @return the updated `UserProfileDTO` object
     */
    @PutMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<UserProfileDTO> updateUserProfile(@Validated @RequestBody UserProfileDTO userProfileDTO) {
        try {
            UserProfileDTO updatedProfile = userService.updateUserProfile(userProfileDTO);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Retrieve the user's role from the session token in cookies.
     *
     * @param request the HTTP request containing cookies
     * @return the role name if valid, or an error message if invalid
     */
    @GetMapping("/getRoleName")
    @ResponseBody
    public ResponseEntity<String> getRoleName(HttpServletRequest request) {
        String token = getTokenFromCookies(request);

        if (token == null || !sessionTokenService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing session token.");
        }

        return ResponseEntity.ok(sessionTokenService.getRoleFromToken(token));
    }

    /**
     * Upload a temporary avatar for the user.
     *
     * @param file the uploaded image file
     * @return the URL of the uploaded image
     */
    @PostMapping("/profile/avatar/temp")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadTemporaryAvatar(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            if (file.isEmpty() || !Objects.requireNonNull(file.getContentType()).matches("image/(jpeg|png|jpg|gif)")) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "Invalid image format. Only JPEG, PNG, JPG, and GIF are allowed."));
            }

            // Validate file size (e.g., max 5 MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "File size exceeds the allowed limit of 5MB."));
            }

            // Save the file temporarily
            String fileUrl = saveFileToStaticDirectory(file);

            // Optionally schedule deletion after 1 hour
            scheduleFileDeletion(fileUrl);

            return ResponseEntity.ok(Collections.singletonMap("url", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to upload image."));
        }
    }

    /**
     * Extracts the session token from cookies.
     *
     * @param request the HTTP request containing cookies
     * @return the session token if found, or null otherwise
     */
    private String getTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Saves a file to the `static/plugin/images` directory.
     *
     * @param file the file to be saved
     * @return the URL of the saved file
     * @throws IOException if an error occurs during file saving
     */
    private String saveFileToStaticDirectory(MultipartFile file) throws IOException {
        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9._-]", "_");
        String fileName = UUID.randomUUID() + "_" + originalFileName;

        Path imageDirectory = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "plugin", "images");
        if (!Files.exists(imageDirectory)) {
            Files.createDirectories(imageDirectory);
        }

        Path filePath = imageDirectory.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/plugin/images/" + fileName;
    }

    private void scheduleFileDeletion(String fileUrl) {
        String fileName = fileUrl.replace("/plugin/images/", "");
        Path filePath = Paths.get("src/main/resources/static/plugin/images", fileName);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Files.deleteIfExists(filePath);
                    System.out.println("Temporary file deleted: " + filePath);
                } catch (IOException e) {
                    System.err.println("Failed to delete temporary file: " + filePath);
                    e.printStackTrace();
                }
            }
        }, 60 * 60 * 1000); // 1 giờ (đổi giá trị nếu cần)
    }

    //Endpoint to check if image exists in the correct folder or not
    @GetMapping("/plugin/images/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<?> serveImage(@PathVariable String fileName) {
        try {
            Path imagePath = Paths.get("src/main/resources/static/plugin/images", fileName);
            if (!Files.exists(imagePath)) {
                // Return default image if not found image in database
                imagePath = Paths.get("src/main/resources/static/plugin/images/default_avatar.jpg");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                    .body(Files.readAllBytes(imagePath));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load image.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Lấy session token từ cookie trong request
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("SESSIONID".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Nếu token tồn tại, xóa nó khỏi sessionStore (server-side)
        if (token != null) {
            sessionTokenService.deleteSession(token);

            // Xóa cookie trên client-side
            Cookie logoutCookie = new Cookie("SESSIONID", null);
            logoutCookie.setPath("/"); // Đảm bảo path của cookie chính xác
            logoutCookie.setMaxAge(0); // Set maxAge = 0 để xóa cookie ngay lập tức
            response.addCookie(logoutCookie);
        }

        // Trả về thông báo thành công
        return ResponseEntity.ok("Successfully logged out");
    }



}
