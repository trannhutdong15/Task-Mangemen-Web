package task_management_web.task_management_web.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TaskUpdates")
@Data
public class TaskUpdatesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    private String status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by_name")
    private String updatedByName;

    @Column(name = "previous_status")
    private String previousStatus;

    // Set relationship to Task
    //A single task can be updated multiple times
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    // Relationship with User
    //One user can update multiple tasks
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    //Table in the middle to get a list of previous users who join in a task
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_update_previous_users",
            joinColumns = @JoinColumn(name = "task_update_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> previousAssignedUsers = new HashSet<>();




}
