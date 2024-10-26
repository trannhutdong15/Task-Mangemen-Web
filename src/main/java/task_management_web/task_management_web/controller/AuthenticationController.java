package task_management_web.task_management_web.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_management_web.task_management_web.DTO.LoginDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Constructor injection
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //Post method for Register
    @PostMapping("/register")
    public ResponseEntity<String> registerUser( @Valid @RequestBody UserDTO userDTO) {

        if (authenticationService.register(userDTO)) {
            return new ResponseEntity<>("Register successfully" , HttpStatus.OK);
        }
        return new ResponseEntity<>("Register failed", HttpStatus.BAD_REQUEST);
    }

    //Post method for Login
    @PostMapping("/login")
        public ResponseEntity<String> loginUser (@Valid @RequestBody LoginDTO loginDTO) {
        authenticationService.login(loginDTO);
        return new ResponseEntity<>("Login successfully", HttpStatus.ACCEPTED);
        }
}
