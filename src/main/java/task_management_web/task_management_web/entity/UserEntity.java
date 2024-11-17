package task_management_web.task_management_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    private String full_name;
    private String first_name;
    private String last_name;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String address;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String avatarUrl;

    @ManyToOne(fetch = FetchType.EAGER) // Thiết lập mối quan hệ nhiều-nhiều với bảng Role
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore// Liên kết với cột id của bảng Role
    private RoleEntity role;

    //Status of a new account
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;  // Mặc định là "chờ phê duyệt"

    public enum Status {
        PENDING,
        APPROVED,
    }
    //Link to UserWorkAreasEntity
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<UserWorkAreasEntity> userWorkAreas;

    @ManyToMany(mappedBy = "assignedTo")
    @JsonIgnore
    private List<TaskEntity> tasks;
}
