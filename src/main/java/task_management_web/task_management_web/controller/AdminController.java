package task_management_web.task_management_web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.exception.ResourceNotFoundException;
import task_management_web.task_management_web.exception.UserNotFoundException;
import task_management_web.task_management_web.service.AdminService;
import task_management_web.task_management_web.service.TaskService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final TaskService taskService;

    public AdminController(AdminService adminService, TaskService taskService) {
        this.adminService = adminService;
        this.taskService = taskService;
    }


    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }


    @GetMapping("/pending-users")
    @ResponseBody
    public List<UserDTO> getPendingUsers() {
        return adminService.getFilteredPendingUsers();
    }


    @PutMapping("/approve")
    @ResponseBody
    public String approveAccount(@RequestParam("userId") int userId) {
        adminService.updateStatus(userId, UserEntity.Status.APPROVED);
        return "redirect:/admin/pending-users";
    }


    @PutMapping("/assign-role")
    @ResponseBody
    public ResponseEntity<String> assignRole(@RequestParam("userId") int userId, @RequestParam("roleId") int roleId) {
        adminService.assignRole(userId, roleId);
        return ResponseEntity.ok("Role assigned successfully");
    }


    @PutMapping("/assign-workarea")
    @ResponseBody
    public ResponseEntity<String> assignWorkArea(@RequestParam("userId") int userId,
                                                 @RequestParam("workAreaId") String workAreaId,
                                                 @RequestParam("roleType") String roleType) {
        try {
            adminService.assignWorkArea(userId, workAreaId, roleType);
            return ResponseEntity.ok("Work area and role assigned successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while assigning work area.");
        }
    }


    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteUser(@RequestParam("userId") int userId) {
        adminService.deleteUser(userId);
        return "redirect:/admin/pending-users";
    }

    @GetMapping("/getTasks")
    @ResponseBody
    public ResponseEntity<List<TaskDTO>> getTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();

        // Nếu không tìm thấy task, ném exception
        if (tasks.isEmpty()) {
            throw new ResourceNotFoundException("No tasks found.");
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserDTO> getUsers() {
        return adminService.getAllUsers();
    }

}
