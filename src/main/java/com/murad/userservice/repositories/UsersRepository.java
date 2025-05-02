package com.murad.userservice.repositories;

import com.murad.userservice.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity,Long> {
    Optional<UsersEntity> findByUsernameAndPassword(String username,String password);
    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByMyPromoCode(String promoCode);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
