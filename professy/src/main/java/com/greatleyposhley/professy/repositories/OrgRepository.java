package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Org;
import org.apache.catalina.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgRepository extends CrudRepository<Org, Long> {

    Optional<Org> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<User> findByEmailAndSlug(String email, String slug);

    boolean existsByEmailAndSlug(String email, String slug);
}