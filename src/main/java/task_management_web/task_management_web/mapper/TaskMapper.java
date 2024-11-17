package task_management_web.task_management_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.TaskEntity;
import task_management_web.task_management_web.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Ánh xạ từ TaskEntity sang TaskDTO
    @Mappings({
            @Mapping(source = "assignedTo", target = "assignedUsers", qualifiedByName = "usersToUserDTOs"),
            @Mapping(source = "workArea.id", target = "workAreaId"),
            @Mapping(source = "workArea.name", target = "workAreaName")
    })
    TaskDTO taskToTaskDTO(TaskEntity taskEntity);

    // Ánh xạ từ danh sách UserEntity sang danh sách UserDTO
    @Named("usersToUserDTOs")
    default List<UserDTO> usersToUserDTOs(List<UserEntity> users) {
        return users != null ? users.stream().map(this::userToUserDTO).collect(Collectors.toList()) : null;
    }

    // Ánh xạ từ UserEntity sang UserDTO
    @Mapping(source = "role.role", target = "roleName") // Ánh xạ tên role
    UserDTO userToUserDTO(UserEntity user);

    // Ánh xạ từ TaskDTO sang TaskEntity
    @Mappings({
            @Mapping(source = "assignedUsers", target = "assignedTo", qualifiedByName = "userDTOsToUsers"),
            @Mapping(target = "subTasks", ignore = true), // Bỏ qua các trường không cần thiết
            @Mapping(target = "workArea", ignore = true) // Thiết lập workArea trong Service
    })
    TaskEntity taskDTOToTaskEntity(TaskDTO taskDTO);

    // Ánh xạ từ danh sách UserDTO sang danh sách UserEntity
    @Named("userDTOsToUsers")
    default List<UserEntity> userDTOsToUsers(List<UserDTO> userDTOs) {
        return userDTOs != null ? userDTOs.stream().map(this::userDTOToUser).collect(Collectors.toList()) : null;
    }

    // Ánh xạ từ UserDTO sang UserEntity
    UserEntity userDTOToUser(UserDTO userDTO);
}
