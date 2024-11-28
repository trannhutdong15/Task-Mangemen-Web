package task_management_web.task_management_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    //Custom query that finds user account status in the database
    List<UserEntity> findByStatus(UserEntity.Status status);

    boolean existsByEmail(String email);//Find email that exists in the database or not
    Optional<UserEntity> findByEmail(String email);


    boolean existsByPhoneNumber(String phoneNumber);

    //Find User by Work Area id
    @Query("SELECT u FROM UserEntity u JOIN u.userWorkAreas uwa JOIN uwa.workAreas wa WHERE wa.id = :workAreaId")
    List<UserEntity> findUsersByWorkAreaId(@Param("workAreaId") String workAreaId);

    //Find Role and work area id query
    List<UserEntity> findByRole_RoleAndUserWorkAreas_WorkAreas_Id(String role, String workAreaId);

    //find user id by users' emails
    @Query("SELECT u.id FROM UserEntity u WHERE u.email = :email")
    Optional<Integer> findIdByEmail(String email);

}
