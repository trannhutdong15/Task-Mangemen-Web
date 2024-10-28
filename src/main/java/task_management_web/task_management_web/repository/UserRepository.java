package task_management_web.task_management_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task_management_web.task_management_web.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    //Custom query that find user account status in the database
    List<UserEntity> findByStatus(UserEntity.Status status);
    boolean existsByEmail(String email);//Find email that exists in the database or not
    Optional<UserEntity> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
