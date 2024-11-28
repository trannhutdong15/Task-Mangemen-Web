package task_management_web.task_management_web.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.RoleEntity;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.entity.UserWorkAreasEntity;
import task_management_web.task_management_web.entity.WorkAreasEntity;
import task_management_web.task_management_web.exception.UserNotFoundException;
import task_management_web.task_management_web.mapper.UserMapper;
import task_management_web.task_management_web.repository.RoleRepository;
import task_management_web.task_management_web.repository.UserRepository;
import task_management_web.task_management_web.repository.UserWorkAreaRepository;
import task_management_web.task_management_web.repository.WorkAreasRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final WorkAreasRepository workAreasRepository;

    private final UserWorkAreaRepository userWorkAreaRepository;

    private final RoleRepository roleRepository;



    //Constructor to call packages

    public AdminService(UserRepository userRepository , UserMapper userMapper , UserWorkAreaRepository userWorkAreaRepository , WorkAreasRepository workAreasRepository , RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.workAreasRepository = workAreasRepository;
        this.userWorkAreaRepository = userWorkAreaRepository;
        this.roleRepository = roleRepository;
    }

    //Get all Pending Request to Admin view
    @Transactional
    public List<UserDTO> getFilteredPendingUsers() {
        List<UserEntity> pendingUsers = userRepository.findByStatus(UserEntity.Status.PENDING);
        return pendingUsers.stream()
                .map(user -> {
                    UserDTO userDTO = userMapper.toDTO(user);
                    UserDTO filteredDTO = new UserDTO();
                    filteredDTO.setId(user.getId());
                    filteredDTO.setEmail(userDTO.getEmail());
                    filteredDTO.setAddress(userDTO.getAddress());
                    filteredDTO.setFull_name(userDTO.getFull_name());
                    filteredDTO.setCreatedAt(userDTO.getCreatedAt());
                    filteredDTO.setPhoneNumber(userDTO.getPhoneNumber());
                    return filteredDTO;
                })
                .collect(Collectors.toList());
    }

    //Assign role function for admin to assign a specific role to users
    @Transactional
    public void assignRole(int userId, int roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserNotFoundException("Role not found with id " + roleId));

        user.setRole(role);
        userRepository.save(user);
    }


    //Update Users status for them to log in (verify accounts)
    @Transactional
    public void updateStatus(int userId, UserEntity.Status status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        user.setStatus(status);
        userRepository.save(user);
    }

    //Assign Users to correct work area that they applied
    @Transactional
    public void assignWorkArea(int userId, String workAreaId, String roleType) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        WorkAreasEntity workArea = workAreasRepository.findById(workAreaId)
                .orElseThrow(() -> new UserNotFoundException("Work area not found with id " + workAreaId));

        // Check User already have work area or not
        Optional<UserWorkAreasEntity> existingWorkArea = userWorkAreaRepository.findByUser(user);

        UserWorkAreasEntity userWorkArea;

        if (existingWorkArea.isPresent()) {
            // If existed, update
            userWorkArea = existingWorkArea.get();
        } else {
            // If not, create new data
            userWorkArea = new UserWorkAreasEntity();
            userWorkArea.setUser(user);
        }
        userWorkArea.setWorkAreas(workArea);
        userWorkArea.setRoleType(roleType);

        userWorkAreaRepository.save(userWorkArea);
    }

    //If admin rejects a user, it will remove them out of a database
    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }


    // Get a list of users' info
    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();

                    // Attached basic users' information
                    userDTO.setId(user.getId());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setFull_name(user.getFull_name());
                    userDTO.setAddress(user.getAddress());
                    userDTO.setPhoneNumber(user.getPhoneNumber());

                    // Get user roleName if they don't, have to assign Not Assigned
                    String roleName = user.getRole() != null ? user.getRole().getRole() : "Not Assigned";
                    userDTO.setRoleName(roleName);

                    // Get user work area name
                    String workAreaName = user.getUserWorkAreas().stream()
                            .findFirst() // Lấy phân khu đầu tiên (chỉ có một phân khu cho mỗi người dùng)
                            .map(userWorkArea -> userWorkArea.getWorkAreas().getName())
                            .orElse("Not Assigned");
                    userDTO.setWorkAreaName(workAreaName);

                    return userDTO; // return a DTO list to controller
                })
                .collect(Collectors.toList());
    }

}
