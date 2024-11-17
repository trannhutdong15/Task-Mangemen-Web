package task_management_web.task_management_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.entity.TaskEntity;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("SELECT u.full_name FROM TaskEntity t JOIN t.assignedTo u WHERE t.id = :taskId")
    List<String> findAssignedUserFullNameByTaskId(@Param("taskId") Long taskId);
}
