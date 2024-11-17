package task_management_web.task_management_web.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaskDTO {

    private Long id;  // Đổi thành Integer nếu muốn sử dụng int thay vì Long
    private String title;
    private String description;

    private List<String> memberNames;

    private List<UserDTO> assignedUsers = new ArrayList<>(); ;  // Danh sách người dùng được giao nhiệm vụ

    @JsonProperty("deadline")
    private LocalDate deadline;

    @JsonProperty("createdAt")
    private LocalDate createdAt;

    private String status;

    private String workAreaId;  // Đổi thành Integer để tương thích với cấu trúc mới

    private String workAreaName;  // Tên của khu vực làm việc

    // Các thông tin khác của task nếu cần thêm
}
