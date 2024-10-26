package task_management_web.task_management_web.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

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
    private String phone_number;

    private String address;

    private String status;

    private LocalDate created_at;

    private int roleId;
}
