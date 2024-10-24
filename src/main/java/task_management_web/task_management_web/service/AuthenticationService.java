package task_management_web.task_management_web.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.mapper.UserMapper;
import task_management_web.task_management_web.repository.UserRepository;

@Service
public class AuthenticationService {


    //Using constructor-based injection
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    // Check whether users exist in the database
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Function for register
    public boolean register (UserDTO userDTO) {

        if(userRepository.existsByEmail(userDTO.getEmail())) {
            return false;
        }
        // Convert DTO to Entity when working with Repository (User)
        UserEntity newuserEntity = userMapper.toEntity(userDTO);
        newuserEntity.setPassword(passwordEncoder.encode(newuserEntity.getPassword()));
        userRepository.save(newuserEntity);
        return true;
    }

    // Function Login Logic
    public boolean login (UserDTO userDTO) {

        UserEntity userEntity = userRepository.findByEmail(userDTO.getEmail())
                .orElse(null);

        // Return true if found user, false if not found
        return userEntity != null && passwordEncoder.matches(userDTO.getPassword(), userEntity.getPassword());
    }

    // Save user in the database when admin confirm the account
    public void save(UserEntity user) {
        userRepository.save(user);
    }
}
