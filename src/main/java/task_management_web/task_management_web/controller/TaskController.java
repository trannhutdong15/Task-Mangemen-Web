package task_management_web.task_management_web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.service.TaskService;
import task_management_web.task_management_web.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final UserService userService;
    private final TaskService taskService;

    // Constructor Injection
    @Autowired
    public TaskController(UserService userService,TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    //Get List of active Users in same role as TeamLeader
    @GetMapping("/staff")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getStaffUsersByWorkArea(@RequestParam("workAreaId") String workAreaId) {
        try {
            List<UserDTO> staffUsers = userService.getStaffUsersByWorkArea(workAreaId);
            return ResponseEntity.ok(staffUsers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/create-task")
    @ResponseBody
    public ResponseEntity<String> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            taskService.createTask(taskDTO); // Đơn giản chỉ gọi service để lưu task
            return ResponseEntity.ok("Task created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the task.");
        }
    }


    @GetMapping("/dashboard")
    @ResponseBody
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllAssignedTasks();
    }
}
