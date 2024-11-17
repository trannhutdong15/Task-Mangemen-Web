package task_management_web.task_management_web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    @NotBlank(message = "Id required")
    private Integer id;

    @NotBlank(message = "Your full name is required")
    private String full_name;

    @NotBlank(message = "Your first name is required")
    private String first_name;

    @NotBlank(message = "Your last name is required")
    private String last_name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Your phone number is required")
    private String phoneNumber;

    private String address;

    private String status;

    private String avatarUrl;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    private int roleId;

    private String roleName;

    // Các trường mới cho WorkArea
    private String workAreaId;

    private String workAreaName;

    private List<AssignedTaskDTO> assignedTasks;

}

