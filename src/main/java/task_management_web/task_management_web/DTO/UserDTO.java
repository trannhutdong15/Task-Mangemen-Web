package task_management_web.task_management_web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;

    @NotBlank(message = "Your full name is required")
    private String full_name;

    @NotBlank(message = "Your first name is required")
    private String first_name;

    @NotBlank(message = "Your last name is required")
    private String last_name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Phone number cannot be null")
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

