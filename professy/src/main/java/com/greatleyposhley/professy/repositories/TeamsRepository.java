package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, Long> {

    List<Teams> findAllByUserAccountId(Long userAccountId);

    Optional<Teams> findByIdAndUserAccountId(Long id, Long userAccountId);

    boolean existsByNameAndUserAccountId(String name, Long userAccountId);

}