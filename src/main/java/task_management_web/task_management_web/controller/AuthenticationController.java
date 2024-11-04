package task_management_web.task_management_web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import task_management_web.task_management_web.DTO.LoginDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.service.AuthenticationService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Constructor injection
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //Log In Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login"; // Thymeleaf sẽ tự động tìm kiếm file login.html trong thư mục templates
    }

    //Register Page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "user/register";
    }


    //Post method for Register
    @PostMapping("/register_validate")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {

        if (authenticationService.register(userDTO)) {
            return new ResponseEntity<>("Register successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Register failed", HttpStatus.BAD_REQUEST);
    }

    //Post method for Login
    @PostMapping("/login_validate")

    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {

        try {
            // Authenticate user and obtain session token
            String token = authenticationService.login(loginDTO);


            // Create a cookie with the session token
            Cookie cookie = new Cookie("SESSIONID", token);
            cookie.setHttpOnly(true);   // Prevent JavaScript access for security
            cookie.setPath("/");        // Make the cookie available site-wide
            cookie.setMaxAge(60 * 60 * 10); // Set cookie lifespan to 10 hours

            response.addCookie(cookie); // Add cookie to response

            // Prepare JSON response with token and role
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("token", token);

            return ResponseEntity.ok(responseBody); // Return JSON response

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
