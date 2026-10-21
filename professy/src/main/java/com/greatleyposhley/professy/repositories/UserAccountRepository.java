package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.OrgRole;
import com.greatleyposhley.professy.entities.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

    Optional<UserAccount> findByOrgIdAndEmail(Long orgId, String email);

    Optional<UserAccount> findByEmail(String email);

    List<UserAccount> findByOrgId(Long orgId);

    List<UserAccount> findByOrgIdAndIsActive(Long orgId, Boolean isActive);

    List<UserAccount> findByOrgIdAndOrgRole(Long orgId, OrgRole orgRole);

    Optional<UserAccount> findByOrgIdAndId(Long orgId, Long id);

}
