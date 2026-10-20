
package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    List<Roles> findAllByOrgId(Long orgId);

    Optional<Roles> findByOrgIdAndId(Long orgId, Long id);

    Optional<Roles> findByOrgIdAndName(Long orgId, String name);

    boolean existsByOrgIdAndName(Long orgId, String name);

    boolean existsByOrgIdAndId(Long orgId, Long id);

    void deleteByOrgIdAndId(Long orgId, Long id);
}
