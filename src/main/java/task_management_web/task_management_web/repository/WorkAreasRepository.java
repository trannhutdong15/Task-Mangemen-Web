package task_management_web.task_management_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.entity.WorkAreasEntity;

@Repository
public interface WorkAreasRepository extends JpaRepository<WorkAreasEntity, String> {
}
