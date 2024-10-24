package task_management_web.task_management_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Mapping roleId in UserDTO to role.id in UserEntity
    @Mapping(target = "role.id", source = "roleId")

    // Mapping status (String in DTO) to status (Enum in Entity)
    @Mapping(target = "status", expression = "java(MapStatustoEnum(userDTO.getStatus()))")

    // Ignore password field during mapping to Entity for later encoding
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserDTO userDTO);

    // Mapping role.id in UserEntity to roleId in UserDTO
    @Mapping(target = "roleId", source = "role.id")

    // Mapping status (Enum in Entity) to status (String in DTO)
    @Mapping(target = "status", expression = "java(MapEnumtoString(userEntity.getStatus()))")
    UserDTO toDTO (UserEntity userEntity);

    // Convert String to Enum for status field
    default UserEntity.Status MapStatustoEnum(String status) {
        return status != null ? UserEntity.Status.valueOf(status) : null;
    }

    // Convert Enum to String for status field
    default String MapEnumtoString(UserEntity.Status status) {
        return status != null ? status.name() : null;
    }
}
