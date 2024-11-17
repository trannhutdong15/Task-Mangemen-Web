package task_management_web.task_management_web.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.entity.TaskEntity;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.mapper.TaskMapper;
import task_management_web.task_management_web.repository.TaskRepository;
import task_management_web.task_management_web.repository.UserRepository;
import task_management_web.task_management_web.repository.WorkAreasRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {


    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final WorkAreasRepository workAreasRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
                       WorkAreasRepository workAreasRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.workAreasRepository = workAreasRepository;
        this.userRepository = userRepository;
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
                            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userDTO.getId())))
                    .collect(Collectors.toList());
            taskEntity.setAssignedTo(assignedUsers);
        } else {
            System.out.println("No assigned users found.");
        }

        // Save task
        taskRepository.save(taskEntity);
    }



    public List<TaskDTO> getAllAssignedTasks() {
        List<TaskEntity> taskEntities = taskRepository.findAll();

        return taskEntities.stream()
                .map(task -> {
                    TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);

                    // Sử dụng query custom để lấy danh sách full_name
                    List<String> memberNames = taskRepository.findAssignedUserFullNameByTaskId(task.getId());
                    taskDTO.setMemberNames(memberNames); // Đặt danh sách tên vào DTO

                    return taskDTO;
                })
                .collect(Collectors.toList());
    }
}
