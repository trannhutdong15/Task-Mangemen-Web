package task_management_web.task_management_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import task_management_web.task_management_web.DTO.TaskAssignedUserDTO;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.DTO.TaskUpdatesDTO;
import task_management_web.task_management_web.entity.TaskEntity;
import task_management_web.task_management_web.entity.TaskUpdatesEntity;
import task_management_web.task_management_web.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    // Ánh xạ từ TaskEntity sang TaskDTO
    @Mappings({
            @Mapping(source = "assignedTo", target = "assignedUsers", qualifiedByName = "mapAssignedUsers"),
            @Mapping(source = "workArea.id", target = "workAreaId"),
            @Mapping(source = "workArea.name", target = "workAreaName")
    })
    TaskDTO taskToTaskDTO(TaskEntity taskEntity);

    // Ánh xạ từ TaskDTO sang TaskEntity
    @Mappings({
            @Mapping(source = "assignedUsers", target = "assignedTo", qualifiedByName = "mapToUserEntities"),
            @Mapping(target = "subTasks", ignore = true), // Bỏ qua các trường không cần thiết
            @Mapping(target = "workArea", ignore = true) // Thiết lập workArea trong Service
    })
    TaskEntity taskDTOToTaskEntity(TaskDTO taskDTO);

    // Ánh xạ từ danh sách UserEntity sang danh sách TaskAssignedUserDTO
    @Named("mapAssignedUsers")
    default List<TaskAssignedUserDTO> mapAssignedUsers(List<UserEntity> users) {
        return users != null
                ? users.stream().map(user -> new TaskAssignedUserDTO(user.getId(), user.getFull_name(), user.getAvatarUrl())).collect(Collectors.toList())
                : null;
    }

    // Ánh xạ ngược từ TaskAssignedUserDTO sang UserEntity
    @Named("mapToUserEntities")
    default List<UserEntity> mapToUserEntities(List<TaskAssignedUserDTO> assignedUsers) {
        return assignedUsers != null
                ? assignedUsers.stream().map(userDTO -> {
            UserEntity user = new UserEntity();
            user.setId(userDTO.getId());
            user.setFull_name(userDTO.getFull_name());
            user.setAvatarUrl(userDTO.getAvatarUrl());
            return user;
        }).collect(Collectors.toList())
                : null;
    }

    //Mapping TaskUpdatesEntity
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "task.title", target = "taskTitle") // Map thêm tiêu đề Task
    @Mapping(source = "user.full_name", target = "userFullName") // Map thêm tên đầy đủ của User
    TaskUpdatesDTO taskUpdatesEntityToTaskUpdatesDTO(TaskUpdatesEntity entity);

    @Mapping(source = "taskId", target = "task.id")
    @Mapping(source = "userId", target = "user.id")
    TaskUpdatesEntity taskUpdatesDTOToTaskUpdatesEntity(TaskUpdatesDTO dto);
}

