package task_management_web.task_management_web.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private int id;
    private String full_name;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String phone_number;
    private String address;
    private String status;
    private LocalDate created_at;
    private int roleId;
}
