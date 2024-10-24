package task_management_web.task_management_web.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "UserRoles")
@Getter
@Setter
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "role") // Relation One-to-Many with UserEntity
    private Set<UserEntity> users;

}
