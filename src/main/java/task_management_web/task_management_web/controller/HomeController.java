package task_management_web.task_management_web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/home")
    public String home() {
        return "user/home"; // Trỏ đến file home.html trong thư mục templates/user
    }

}
