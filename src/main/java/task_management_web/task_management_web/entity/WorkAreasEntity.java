package task_management_web.task_management_web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "WorkAreas")
@Getter
@Setter
public class WorkAreasEntity {
    @Id
    @Column(name = "id" , length = 10)
    private String id;

    private String name;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "workAreas")
    private Set<UserWorkAreasEntity> userWorkAreas;
}
