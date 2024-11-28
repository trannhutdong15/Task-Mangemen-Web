package task_management_web.task_management_web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TaskUpdatesDTO {
        private Long id;

        private Long taskId;

        private Integer userId;

        private String status;

        private String previousStatus;

        private String updatedByName;

        @JsonProperty("updatedAt")
        private LocalDateTime updatedAt;

        private String taskTitle;

        private String userFullName;

}
