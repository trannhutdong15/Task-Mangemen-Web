package task_management_web.task_management_web.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.TaskAssignedUserDTO;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.entity.TaskEntity;
import task_management_web.task_management_web.entity.TaskUpdatesEntity;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.exception.ResourceNotFoundException;
import task_management_web.task_management_web.exception.UserNotFoundException;
import task_management_web.task_management_web.mapper.TaskMapper;
import task_management_web.task_management_web.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static task_management_web.task_management_web.service.AuthenticationService.logger;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final WorkAreasRepository workAreasRepository;
    private final UserRepository userRepository;
    private final TaskUpdatesRepository taskUpdatesRepository;
    private final UserWorkAreaRepository userWorkAreaRepository;


    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       WorkAreasRepository workAreasRepository, UserRepository userRepository,
                       TaskUpdatesRepository taskUpdatesRepository , UserWorkAreaRepository userWorkAreaRepository) {


        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.workAreasRepository = workAreasRepository;
        this.userRepository = userRepository;
        this.taskUpdatesRepository = taskUpdatesRepository;
        this.userWorkAreaRepository = userWorkAreaRepository;


    }

    @Transactional
    public void createTask(TaskDTO taskDTO) {
        TaskEntity taskEntity = taskMapper.taskDTOToTaskEntity(taskDTO);

        // Set Work Area
        if (taskDTO.getWorkAreaId() != null) {
            taskEntity.setWorkArea(workAreasRepository.findById(taskDTO.getWorkAreaId())
                    .orElseThrow(() -> new RuntimeException("Work area not found")));
        }

        // Map assigned users
        if (taskDTO.getAssignedUsers() != null && !taskDTO.getAssignedUsers().isEmpty()) {
            List<UserEntity> assignedUsers = taskDTO.getAssignedUsers().stream()
                    .map(userDTO -> userRepository.findById(userDTO.getId())
                            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userDTO.getId())))
                    .collect(Collectors.toList());
            taskEntity.setAssignedTo(assignedUsers);
        }

        // Save task
        taskRepository.save(taskEntity);
    }

    public TaskDTO getTaskById(Long taskId, Integer userId) {
        // Fetch a task with work area information
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        String taskWorkAreaId = task.getWorkArea().getId();

        // Validate that the user has access to this work area
        boolean hasAccess = userWorkAreaRepository.existsByUserIdAndWorkAreas_Id(userId, taskWorkAreaId);
        if (!hasAccess) {
            throw new AccessDeniedException("You do not have permission to access this task.");
        }

        // Return task details as DTO
        return taskMapper.taskToTaskDTO(task);
    }

    // Get all assigned tasks in specific WorkArea
    public List<TaskDTO> getAllAssignedTasks (String workAreaId) {
        List<TaskEntity> taskEntities = taskRepository.findByWorkAreaId(workAreaId);

        // Map TaskEntities to TaskDTOs and fetch assigned users
        return taskEntities.stream()
                .map(task -> {
                    TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);

                    // Use Custom Query from TaskRepository to find assigned users
                    List<TaskAssignedUserDTO> assignedUsers = taskRepository
                            .findAssignedUserWithTaskId(task.getId());
                    taskDTO.setAssignedUsers(assignedUsers);

                    return taskDTO;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO, HttpSession session) {
        Integer userId = Optional.ofNullable((Integer) session.getAttribute("userId"))
                .orElseThrow(() -> new RuntimeException("User not logged in or session expired"));

        // Fetch current user and task from DB
        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        TaskEntity existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Check access to work area
        if (!userWorkAreaRepository.existsByUserIdAndWorkAreas_Id(userId, existingTask.getWorkArea().getId())) {
            throw new AccessDeniedException("You do not have permission to edit tasks in this work area.");
        }

        // Store previous state for history tracking
        String previousStatus = existingTask.getStatus();
        List<UserEntity> previousAssignedUsers = new ArrayList<>(existingTask.getAssignedTo());

        boolean isUpdated = updateTaskFields(existingTask, taskDTO);  // Update basic fields

        if (taskDTO.getAssignedUsers() != null) {
            isUpdated |= updateAssignedUsers(existingTask, taskDTO.getAssignedUsers());
        }

        // If no update, return the current task as DTO
        if (!isUpdated) {
            return taskMapper.taskToTaskDTO(existingTask);
        }

        // Save updated task and update history
        TaskEntity updatedTask = taskRepository.save(existingTask);
        saveTaskUpdateHistory(updatedTask, currentUser, previousStatus, previousAssignedUsers);

        return taskMapper.taskToTaskDTO(updatedTask);
    }

    // Update basic fields of the task (title, description, etc.)
    private boolean updateTaskFields(TaskEntity existingTask, TaskDTO taskDTO) {
        boolean isUpdated = false;

        if (taskDTO.getTitle() != null && !taskDTO.getTitle().equals(existingTask.getTitle())) {
            existingTask.setTitle(taskDTO.getTitle());
            isUpdated = true;
        }
        if (taskDTO.getDescription() != null && !taskDTO.getDescription().equals(existingTask.getDescription())) {
            existingTask.setDescription(taskDTO.getDescription());
            isUpdated = true;
        }
        if (taskDTO.getStatus() != null && !taskDTO.getStatus().equals(existingTask.getStatus())) {
            existingTask.setStatus(taskDTO.getStatus());
            isUpdated = true;
        }
        if (taskDTO.getDeadline() != null && !taskDTO.getDeadline().equals(existingTask.getDeadline())) {
            existingTask.setDeadline(taskDTO.getDeadline());
            isUpdated = true;
        }

        return isUpdated;
    }

    // Update assigned users: add new users and remove old ones
    private boolean updateAssignedUsers(TaskEntity existingTask, List<TaskAssignedUserDTO> assignedUsers) {
        // Lấy danh sách userId hiện tại từ task
        Set<Integer> currentAssignedUserIds = existingTask.getAssignedTo().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        // Lấy danh sách userId mới từ FE truyền về
        Set<Integer> newAssignedUserIds = assignedUsers.stream()
                .map(TaskAssignedUserDTO::getId)
                .collect(Collectors.toSet());

        // Xác định những user cần thêm vào và cần xóa đi
        Set<Integer> usersToAdd = newAssignedUserIds.stream()
                .filter(id -> !currentAssignedUserIds.contains(id))
                .collect(Collectors.toSet());

        Set<Integer> usersToRemove = currentAssignedUserIds.stream()
                .filter(id -> !newAssignedUserIds.contains(id))
                .collect(Collectors.toSet());

        boolean isUpdated = false;

        // Thêm user mới
        if (!usersToAdd.isEmpty()) {
            for (Integer userId : usersToAdd) {
                UserEntity user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
                existingTask.getAssignedTo().add(user);
                logger.info("Assigned user added: {}", user.getFull_name());
            }
            isUpdated = true;
        }

        // Xóa user cũ
        if (!usersToRemove.isEmpty()) {
            existingTask.getAssignedTo().removeIf(user -> usersToRemove.contains(user.getId()));
            logger.info("Removed users: {}", usersToRemove);
            isUpdated = true;
        }

        return isUpdated;
    }

    // Save task update history
    private void saveTaskUpdateHistory(TaskEntity updatedTask, UserEntity currentUser, String previousStatus, List<UserEntity> previousAssignedUsers) {
        TaskUpdatesEntity taskUpdate = new TaskUpdatesEntity();
        taskUpdate.setTask(updatedTask);
        taskUpdate.setUser(currentUser);
        taskUpdate.setUpdatedByName(currentUser.getFull_name());
        taskUpdate.setUpdatedAt(LocalDateTime.now());
        taskUpdate.setStatus(updatedTask.getStatus());
        taskUpdate.setPreviousStatus(previousStatus);
        taskUpdate.setPreviousAssignedUsers(new HashSet<>(previousAssignedUsers));
        taskUpdatesRepository.save(taskUpdate);
    }



    @Transactional
    public void deleteTask(Long taskId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new ResourceNotFoundException("User not logged in or session expired");
        }

        UserEntity currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        // Delete task update first indicates with a specific task
        taskUpdatesRepository.deleteByTaskId(taskId);

        // Create new task update with that specific task
        TaskUpdatesEntity taskUpdate = new TaskUpdatesEntity();
        taskUpdate.setTask(taskEntity);
        taskUpdate.setUser(currentUser);
        taskUpdate.setStatus("Deleted");
        taskUpdate.setPreviousStatus(taskEntity.getStatus());
        taskUpdate.setUpdatedByName(currentUser.getFull_name());
        taskUpdate.setUpdatedAt(LocalDateTime.now());

        taskUpdatesRepository.save(taskUpdate);

        // Lastly delete a task
        taskRepository.delete(taskEntity);
    }
}

