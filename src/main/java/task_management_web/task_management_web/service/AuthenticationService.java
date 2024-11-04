package task_management_web.task_management_web.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.LoginDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.RoleEntity;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.exception.AccountAlreadyExistException;
import task_management_web.task_management_web.exception.AccountNotApprovedException;
import task_management_web.task_management_web.exception.AuthenticationFailedException;
import task_management_web.task_management_web.exception.UserNotFoundException;
import task_management_web.task_management_web.mapper.UserMapper;
import task_management_web.task_management_web.repository.RoleRepository;
import task_management_web.task_management_web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;


@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    private final SessionTokenService sessionTokenService;

    //Using logger for exception error
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    //Using constructor injection
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,RoleRepository roleRepository , SessionTokenService sessionTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.sessionTokenService = sessionTokenService;
        this.roleRepository = roleRepository;
    }


    // Check whether users exist in the database
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Function for register
    public boolean register (UserDTO userDTO) {

        try {
            if(existsByEmail(userDTO.getEmail())) {
                throw new AccountAlreadyExistException("Email already exists");
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }

            if(userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
                throw new AccountAlreadyExistException("Phone number already exists");
            }

            // Convert DTO to Entity when working with Repository (User)
            UserEntity newuserEntity = userMapper.toEntity(userDTO);

            //Set user status to Pending when they register
            if (newuserEntity.getStatus() == null) {
                newuserEntity.setStatus(UserEntity.Status.PENDING);
            }

            //Set user role to Staff when they register an account for first time
            RoleEntity defaultRole = roleRepository.findByRole("Staff")
                    .orElseThrow(() -> new RuntimeException(" Role Staff not found"));
            newuserEntity.setRole(defaultRole);


            //Encoded password before save to database
            newuserEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            newuserEntity.setCreatedAt(LocalDateTime.now());

            userRepository.save(newuserEntity);
            return true;

        }
        //Print out the error if exists
        catch (Exception e) {
            logger.error("An error occurred: ", e);
            return false;
        }

    }

    // Function Login Logic
    public String login (@NotNull LoginDTO loginDTO) {

        // Find User in Repository or in database if not found throw exception
        UserEntity userEntity = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if an account is verified or not
        if (userEntity.getStatus() != UserEntity.Status.APPROVED) {
            throw new AccountNotApprovedException("Account is not approved");
        }

        // Check the password of user accounts see if it matched or not in the database
        if (!passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
        String role = userEntity.getRoleName();
        // Generate session token after successful authentication
        return sessionTokenService.createSessionToken(userEntity.getEmail(),role);
    }

    }

