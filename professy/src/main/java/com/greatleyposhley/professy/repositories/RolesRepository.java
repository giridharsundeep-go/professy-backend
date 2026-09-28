package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByName(String name);
    boolean existsByName(String name);

}
