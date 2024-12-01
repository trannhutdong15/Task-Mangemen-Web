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
        return "user/login";
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



    // Post method for Login
    @PostMapping("/login_validate")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        // Call service to validate
        Map<String, Object> result = authenticationService.login(loginDTO);

        // Create cookie with session token
        Cookie cookie = new Cookie("SESSIONID", result.get("token").toString());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 10); // 10 hours

        response.addCookie(cookie);

        // Prepare response for the client
        Map<String, Object> clientResponse = Map.of(
                "userId", result.get("userId"),
                "roleName", result.get("roleName"),
                "workAreaId", result.get("workAreaId")
        );

        return ResponseEntity.ok(clientResponse);
    }


}
