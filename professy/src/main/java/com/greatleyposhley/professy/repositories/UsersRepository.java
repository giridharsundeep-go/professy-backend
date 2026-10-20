package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {
    List<Users> findAllByOrgId(Long orgId);
    Optional<Users> findByOrgIdAndId(Long orgId, Long id);
    Optional<Users> findByOrgIdAndEmail(Long orgId, String email);
    boolean existsByOrgIdAndEmail(Long orgId, String email);
    boolean existsByOrgIdAndEmailAndIdNot(Long orgId, String email, Long id);
}
