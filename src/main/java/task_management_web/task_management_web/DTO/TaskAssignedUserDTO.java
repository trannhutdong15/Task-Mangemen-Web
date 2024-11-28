package task_management_web.task_management_web.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignedUserDTO {
    private Integer id;
    private String full_name;
    private String avatarUrl;
}

