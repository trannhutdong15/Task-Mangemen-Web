package task_management_web.task_management_web.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    @NotNull(message = "Id is required")
    private Integer id;

    @NotBlank(message = "Your full name is required")
    private String full_name;

    private String first_name;

    @NotBlank(message = "Email is required")
    private String email;

    private String roleName;

    @NotBlank(message = "Your phone number is required")
    private String phoneNumber;

    private String address;

    private String avatarUrl;
}
