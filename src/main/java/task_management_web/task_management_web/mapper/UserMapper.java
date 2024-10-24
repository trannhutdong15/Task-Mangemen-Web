package task_management_web.task_management_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "status" , source = "status")

    //ignore password for further encode password logic
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserDTO userDTO);

    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "status" , source = "status")
    UserDTO toDTO(UserEntity userEntity);

    //Convert String to Enum values
    default UserEntity.Status MapStatustoEnum(String status) {
        return status != null ? UserEntity.Status.valueOf(status) : null;
    }

    //Convert Enum to String
    default String MapEnumtoString(UserEntity.Status status) {
        return status != null ? status.name() : null;
    }
}
