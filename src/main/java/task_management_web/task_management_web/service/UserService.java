package task_management_web.task_management_web.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.AssignedTaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.DTO.UserProfileDTO;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.exception.UserNotFoundException;
import task_management_web.task_management_web.mapper.UserMapper;
import task_management_web.task_management_web.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionTokenService sessionTokenService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, SessionTokenService sessionTokenService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.sessionTokenService = sessionTokenService;
    }

    /**
     * Retrieves a list of staff users by work area ID.
     * Filters only users with the "Staff" role.
     *
     * @param workAreaId the ID of the work area
     * @return a list of filtered UserDTO objects
     * @throws UserNotFoundException if no staff users are found
     */
    public List<UserDTO> getStaffUsersByWorkArea(String workAreaId) {
        List<UserEntity> staffUsers = userRepository.findByRole_RoleAndUserWorkAreas_WorkAreas_Id("Staff", workAreaId);

        if (staffUsers.isEmpty()) {
            throw new UserNotFoundException("No staff found for the specified work area.");
        }

        return staffUsers.stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setAvatarUrl(user.getAvatarUrl());
                    userDTO.setFull_name(user.getFull_name());
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users by work area ID.
     * Maps UserEntity to UserDTO while including assigned tasks.
     *
     * @param workAreaId the ID of the work area
     * @return a list of UserDTO objects
     */
    @Transactional
    public List<UserDTO> getAllUsersByWorkAreaId(String workAreaId) {
        // Get List of users by work area id
        List<UserEntity> users = userRepository.findUsersByWorkAreaId(workAreaId);

        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found for the specified work area.");
        }

        // Convert UserEntity to DTO
        return users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setFull_name(user.getFull_name());
                    dto.setEmail(user.getEmail());
                    dto.setAvatarUrl(user.getAvatarUrl());
                    dto.setPhoneNumber(user.getPhoneNumber());

                    // Task Assigned on user
                    List<AssignedTaskDTO> assignedTasks = Optional.ofNullable(user.getTasks())
                            .filter(tasks -> !tasks.isEmpty())
                            .map(tasks -> tasks.stream()
                                    .map(task -> new AssignedTaskDTO(
                                            task.getId(),
                                            task.getTitle(),
                                            task.getDeadline(),
                                            task.getStatus()
                                    ))
                                    .collect(Collectors.toList())
                            )
                            .orElse(Collections.singletonList(
                                    new AssignedTaskDTO(null, "Not Assigned", null, null)
                            ));

                    dto.setAssignedTasks(assignedTasks);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user profile by user ID.
     *
     * @param userId the ID of the user
     * @return the UserProfileDTO object
     */
    @Transactional
    public UserProfileDTO getUserProfileById(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        return userMapper.toProfileDTO(user);
    }

    /**
     * Updates a user's profile information.
     *
     * @param userProfileDTO the updated user profile DTO
     * @return the updated UserProfileDTO object
     */
    @Transactional
    public UserProfileDTO updateUserProfile(UserProfileDTO userProfileDTO) {
        UserEntity user = userRepository.findById(userProfileDTO.getId())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userProfileDTO.getId() + " not found."));

        user.setFull_name(userProfileDTO.getFull_name());
        user.setEmail(userProfileDTO.getEmail());
        user.setPhoneNumber(userProfileDTO.getPhoneNumber());
        user.setAddress(userProfileDTO.getAddress());
        user.setAvatarUrl(userProfileDTO.getAvatarUrl());

        return userMapper.toProfileDTO(userRepository.save(user));
    }

    /**
     * Retrieves the user ID from the session or cookies.
     *
     * @param request the HTTP request containing session and cookies
     * @return the user ID
     * @throws IllegalStateException if the user is not logged in or the session has expired
     */
    public Integer getUserIdFromSessionOrCookie(HttpServletRequest request) {
        Object userIdObj = request.getSession().getAttribute("userId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }

        String sessionToken = getSessionTokenFromCookies(request);
        if (sessionToken != null && sessionTokenService.validateToken(sessionToken)) {
            String username = sessionTokenService.getUsernameFromToken(sessionToken);
            if (username != null) {
                Integer userId = findUserIdByEmail(username);
                request.getSession().setAttribute("userId", userId);
                return userId;
            }
        }

        throw new IllegalStateException("User is not logged in or session has expired.");
    }

    /**
     * Retrieves the session token from cookies.
     *
     * @param request the HTTP request containing cookies
     * @return the session token
     */
    private String getSessionTokenFromCookies(HttpServletRequest request) {
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
     * Finds a user ID by email address.
     *
     * @param email the email address of the user
     * @return the user ID
     * @throws UserNotFoundException if no user is found with the given email
     */
    public Integer findUserIdByEmail(String email) {
        return userRepository.findIdByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
    }
}
