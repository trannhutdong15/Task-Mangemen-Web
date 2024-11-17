package task_management_web.task_management_web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/api")
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/home")
    public String home() {
        return "user/home"; // Trỏ đến file home.html trong thư mục templates/user
    }

    @GetMapping("/workAreaId")
    @ResponseBody
    public ResponseEntity<String> getWorkAreaId(HttpServletRequest request) {
        // Get workAreaId from Session
        String workAreaId = (String) request.getSession().getAttribute("workAreaId");

        // Kiểm tra xem workAreaId đã được lưu đúng chưa
        System.out.println("Retrieved workAreaId from session: " + workAreaId);

        if (workAreaId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Work Area ID not found.");
        }

        return ResponseEntity.ok(workAreaId);
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserDTO> getUsersByWorkAreaId(@RequestParam ("workAreaId") String workAreaId) {
        return userService.getAllUsersByWorkAreaId(workAreaId);
    }

}
