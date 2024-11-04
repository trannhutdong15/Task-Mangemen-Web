package task_management_web.task_management_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task_management_web.task_management_web.DTO.UserWorkAreasDTO;
import task_management_web.task_management_web.entity.UserWorkAreasEntity;

@Mapper(componentModel = "spring")
public interface UserWorkAreasMapper {

    //Convert Entity to DTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "workAreas.id" , target = "workAreaId")
    UserWorkAreasDTO toDTO(UserWorkAreasEntity userWorkAreasEntity);

    // Convert DTO to Entity
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "workAreaId" , target = "workAreas.id")
    UserWorkAreasEntity toEntity(UserWorkAreasDTO userWorkAreaDTO);
}
