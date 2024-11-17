package task_management_web.task_management_web.service;

import jakarta.transaction.Transactional;
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
import java.util.HashMap;
import java.util.Map;


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
    @Transactional
    public Map<String, Object> login(LoginDTO loginDTO) {
        // Tìm kiếm người dùng trong repository hoặc cơ sở dữ liệu; ném ngoại lệ nếu không tìm thấy
        UserEntity userEntity = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Kiểm tra trạng thái tài khoản
        if (userEntity.getStatus() != UserEntity.Status.APPROVED) {
            throw new AccountNotApprovedException("Account is not approved");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        // Lấy tên vai trò và work area
        String roleName = userEntity.getRole().getRole();
        String workAreaId = userEntity.getUserWorkAreas().stream()
                .findFirst()
                .map(userWorkArea -> userWorkArea.getWorkAreas().getId())
                .orElse("Not Assigned");

        // Tạo token phiên với username, roleName và workAreaId
        String token = sessionTokenService.createSessionToken(userEntity.getEmail(), roleName, workAreaId);

        // Chuẩn bị phản hồi dưới dạng bản đồ với token, roleName và workAreaId
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("roleName", roleName);
        responseBody.put("workAreaId", workAreaId);

        return responseBody;
    }
}

