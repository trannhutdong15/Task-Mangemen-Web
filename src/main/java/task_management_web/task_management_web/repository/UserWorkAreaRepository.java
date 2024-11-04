package task_management_web.task_management_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.entity.UserWorkAreasEntity;

@Repository
public interface UserWorkAreaRepository extends JpaRepository<UserWorkAreasEntity , Integer> {


}
