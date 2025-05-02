package com.murad.userservice.repositories;

import com.murad.userservice.entities.SubTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTypeRepository extends JpaRepository<SubTypeEntity, Long> {
}
