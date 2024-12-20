package task_management_web.task_management_web.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.entity.UserEntity;
import task_management_web.task_management_web.entity.UserWorkAreasEntity;

import java.util.Optional;

@Repository
public interface UserWorkAreaRepository extends JpaRepository<UserWorkAreasEntity , Integer> {
    Optional<UserWorkAreasEntity> findByUser(UserEntity user);

    //Find exist user by id and also find work area id of that user
    boolean existsByUserIdAndWorkAreas_Id(Integer userId, String workAreaId);
}
