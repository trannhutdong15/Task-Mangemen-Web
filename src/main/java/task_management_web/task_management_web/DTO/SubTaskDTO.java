package task_management_web.task_management_web.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SubTaskDTO {
    private Long id;
    private String description;
    private String status;
    private int progressPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long assignedUserId;

    private String assignedUserName;
}
