package task_management_web.task_management_web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    //Post method for Register
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        if (authenticationService.register(userDTO)) {
            return new ResponseEntity<>("Register successfully" , HttpStatus.OK);
        }
        return new ResponseEntity<>("Register failed" , HttpStatus.BAD_REQUEST);
    }

    //Post method for Login
    @PostMapping("/login")
        public ResponseEntity<String> loginUser(@RequestBody UserDTO userDTO) {
        if (authenticationService.login(userDTO)) {
            return new ResponseEntity<>("Login successfully" , HttpStatus.OK);
        }
        return new ResponseEntity<>("Login failed" , HttpStatus.BAD_REQUEST);
        }
}
