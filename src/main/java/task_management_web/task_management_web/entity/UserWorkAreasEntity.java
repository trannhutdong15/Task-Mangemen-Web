package task_management_web.task_management_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Table(name = "UserWorkAreas")
@Data
public class UserWorkAreasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    @JsonIgnore
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "work_area_id" , nullable = false)
    @JsonIgnore
    private WorkAreasEntity workAreas;

    @Column(name = "role_type")
    private String roleType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;



}
