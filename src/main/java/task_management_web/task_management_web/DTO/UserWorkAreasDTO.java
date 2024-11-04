package task_management_web.task_management_web.DTO;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserWorkAreasDTO {
    private Long id;
    private Long userId;
    private String workAreaId;
    private String roleType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

