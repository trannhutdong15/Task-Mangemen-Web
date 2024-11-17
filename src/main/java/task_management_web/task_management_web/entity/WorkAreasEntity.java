package task_management_web.task_management_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "WorkAreas")
@Data
public class WorkAreasEntity {
    @Id
    @Column(name = "id" , length = 10)
    private String id;

    private String name;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "workAreas")
    @JsonIgnore
    private Set<UserWorkAreasEntity> userWorkAreas;
}
