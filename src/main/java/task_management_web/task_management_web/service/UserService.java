package task_management_web.task_management_web.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.AssignedTaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.exception.UserNotFoundException;
import task_management_web.task_management_web.mapper.UserMapper;
import task_management_web.task_management_web.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository , UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDTO> getStaffUsersByWorkArea(String workAreaId) throws Exception {
        // Truy vấn danh sách nhân viên có role "Staff" trong work area được chỉ định
        List<UserEntity> staffUsers = userRepository.findByRole_RoleAndUserWorkAreas_WorkAreas_Id("Staff", workAreaId);

        // Kiểm tra xem danh sách có trống không, nếu có thì ném ngoại lệ
        if (staffUsers.isEmpty()) {
            throw new Exception("No staff found for the specified work area.");
        }

        // Chuyển đổi danh sách UserEntity sang danh sách UserDTO và lọc chỉ các trường cần thiết
        return staffUsers.stream()
                .map(user -> {
                    UserDTO userDTO = userMapper.toDTO(user);
                    UserDTO filteredDTO = new UserDTO();
                    filteredDTO.setId(userDTO.getId());
                    filteredDTO.setFull_name(userDTO.getFull_name());
                    return filteredDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserDTO> getAllUsersByWorkAreaId(String workAreaId) {
        List<UserEntity> users = userRepository.findUsersByWorkAreaId(workAreaId);

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found for the specified work area.");
        }

        return users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setFull_name(user.getFull_name());
                    dto.setEmail(user.getEmail());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setAssignedTasks(
                            user.getTasks() == null || user.getTasks().isEmpty()
                                    ? Collections.singletonList(new AssignedTaskDTO(null, "Not Assigned"))
                                    : user.getTasks().stream()
                                    .map(task -> new AssignedTaskDTO(task.getId(), task.getTitle()))
                                    .collect(Collectors.toList())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
