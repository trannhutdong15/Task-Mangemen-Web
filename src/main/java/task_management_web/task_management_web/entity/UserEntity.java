package task_management_web.task_management_web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDate created_at;

    private String avatarUrl;

    @ManyToOne(fetch = FetchType.EAGER) // Thiết lập mối quan hệ nhiều-nhiều với bảng Role
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false) // Liên kết với cột id của bảng Role
    private RoleEntity role;

    //Status of a new account
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;  // Mặc định là "chờ phê duyệt"

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
