package task_management_web.task_management_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import task_management_web.task_management_web.DTO.AssignedTaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.TaskEntity;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.entity.UserWorkAreasEntity;
import task_management_web.task_management_web.entity.RoleEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Individual mapping
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role", qualifiedByName = "roleToRoleName")
    @Mapping(target = "workAreaId", source = "userWorkAreas", qualifiedByName = "mapWorkAreaId")
    @Mapping(target = "workAreaName", source = "userWorkAreas", qualifiedByName = "mapWorkAreaName")
    @Mapping(target = "status", expression = "java(userEntity.getStatus() != null ? userEntity.getStatus().name() : null)")
    @Mapping(target = "assignedTasks", source = "tasks", qualifiedByName = "mapAssignedTasks")
    UserDTO toDTO(UserEntity userEntity);

    // List mapping
    List<UserDTO> toDTOList(List<UserEntity> userEntities);

    // Reverse mapping if needed
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "status", expression = "java(userDTO.getStatus() != null ? UserEntity.Status.valueOf(userDTO.getStatus()) : null)")
    UserEntity toEntity(UserDTO userDTO);

    // Custom mapping methods
    @Named("roleToRoleName")
    static String mapRoleToRoleName(RoleEntity role) {
        return role != null ? role.getRole() : "Not Assigned";
    }

    @Named("mapWorkAreaId")
    static String mapWorkAreaId(Set<UserWorkAreasEntity> userWorkAreas) {
        return (userWorkAreas != null && !userWorkAreas.isEmpty()) ?
                userWorkAreas.iterator().next().getWorkAreas().getId() : "Not Assigned";
    }

    @Named("mapWorkAreaName")
    static String mapWorkAreaName(Set<UserWorkAreasEntity> userWorkAreas) {
        return (userWorkAreas != null && !userWorkAreas.isEmpty()) ?
                userWorkAreas.iterator().next().getWorkAreas().getName() : "Not Assigned";
    }

    @Named("mapAssignedTasks")
    static List<AssignedTaskDTO> mapAssignedTasks (List<TaskEntity> tasks) {
        return tasks != null ? tasks.stream()
                .map(task -> new AssignedTaskDTO(task.getId(), task.getTitle()))
                .collect(Collectors.toList()) : null;
    }
}
