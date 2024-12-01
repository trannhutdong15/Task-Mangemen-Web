package task_management_web.task_management_web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.service.TaskService;
import task_management_web.task_management_web.service.UserService;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final UserService userService;
    private final TaskService taskService;

    public TaskController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    // View user with role Staff in the same work area
    @GetMapping("/staff")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getStaffUsersByWorkArea(@RequestParam String workAreaId) {
        List<UserDTO> staffUsers = userService.getStaffUsersByWorkArea(workAreaId);
        return ResponseEntity.ok(staffUsers);
    }

    // Create task endpoint (only for TeamLeader)
    @PostMapping("/create-task")
    @ResponseBody
    public ResponseEntity<?> createTask(@RequestBody TaskDTO taskDTO) {
        taskService.createTask(taskDTO);
        return ResponseEntity.ok(Collections.singletonMap("message", "Task created successfully!"));
    }

    // Get a list of task to view
    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<List<TaskDTO>> getAllTasks(@RequestParam String workAreaId) {
        List<TaskDTO> tasks = taskService.getAllAssignedTasks(workAreaId);
        return ResponseEntity.ok(tasks);
    }


    //Get a specific task to view in Update Modal (only uses purpose for TeamLeader)
    @GetMapping("/details/{taskId}")
    @ResponseBody
    public ResponseEntity<TaskDTO> getTaskDetails(
            @PathVariable Long taskId,
            HttpSession session) {
        // Extract userId from session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new SessionAuthenticationException("User not logged in or session expired.");
        }

        // Fetch task details
        TaskDTO taskDetails = taskService.getTaskById(taskId, userId);
        return ResponseEntity.ok(taskDetails);
    }


    //Update task information endpoint (only for TeamLeader)
    @PutMapping("/update/{taskId}")
    @ResponseBody
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskDTO taskDTO,
            HttpSession session) {
        TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO, session);
        return ResponseEntity.ok(updatedTask); // Return DTO to FE
    }


    //Delete a task (only for TeamLeader)
    // Endpoint to delete task
    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId, HttpSession session) {
        try {
            taskService.deleteTask(taskId, session);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to delete this task.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the task.");
        }
    }
}
