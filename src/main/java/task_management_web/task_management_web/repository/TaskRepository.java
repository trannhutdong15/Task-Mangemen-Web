package task_management_web.task_management_web.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.DTO.TaskAssignedUserDTO;
import task_management_web.task_management_web.DTO.TaskDTO;
import task_management_web.task_management_web.DTO.UserDTO;
import task_management_web.task_management_web.entity.TaskEntity;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {


    //Custom query to find a list of user DTO assigned to a specific task
    @Query("SELECT new task_management_web.task_management_web.DTO.TaskAssignedUserDTO(u.id, u.full_name, u.avatarUrl) " +
            "FROM TaskEntity t JOIN t.assignedTo u WHERE t.id = :taskId")
    List<TaskAssignedUserDTO> findAssignedUserWithTaskId(@Param("taskId") Long taskId);


    //Find a list of task base on Work Area id (task belongs to specific work area)
    @Query("SELECT t FROM TaskEntity t WHERE t.workArea.id = :workAreaId")
    List<TaskEntity> findByWorkAreaId(@Param("workAreaId") String workAreaId);

    //Get userId from task_assignees table
    @Query("SELECT u.id FROM TaskEntity t JOIN t.assignedTo u WHERE t.id = :taskId")
    List<Long> findUserIdsByTaskId(@Param("taskId") Long taskId);

    //Find work area id of a task
    @Query("SELECT t.workArea.id FROM TaskEntity t WHERE t.id = :taskId")
    String findWorkAreaIdByTaskId(@Param("taskId") Long taskId);

}
