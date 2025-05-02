package com.murad.userservice.repositories;


import com.murad.userservice.entities.UserLoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с таблицей user_login_log.
 */
@Repository
public interface UserLoginLogRepository extends JpaRepository<UserLoginLog, Long> {
}
