package task_management_web.task_management_web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String title;
    private String description;

    private List<String> memberNames;

    private List<TaskAssignedUserDTO> assignedUsers = new ArrayList<>();

    @JsonProperty("deadline")
    private LocalDate deadline;

    @JsonProperty("createdAt")
    private LocalDate createdAt;

    private String status;

    private String workAreaId;

    private String workAreaName;

}
