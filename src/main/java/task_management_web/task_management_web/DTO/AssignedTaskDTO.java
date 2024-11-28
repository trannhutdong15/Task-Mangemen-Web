package task_management_web.task_management_web.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignedTaskDTO {
    private Long taskId;
    private String taskName;
    private LocalDate taskDeadline;
    private String taskStatus;
}