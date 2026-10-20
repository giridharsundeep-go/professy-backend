package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByOrgId(Long orgId);

    List<Board> findByOrgIdAndProjectIdOrderByNameAsc(Long orgId, Long projectId);

    Optional<Board> findByOrgIdAndProjectIdAndName(
            Long orgId,
            Long projectId,
            String name
    );

    boolean existsByOrgIdAndProjectIdAndName(
            Long orgId,
            Long projectId,
            String name
    );

    boolean existsByOrgIdAndProjectIdAndNameAndIdNot(
            Long orgId,
            Long projectId,
            String name,
            Long id
    );

    List<Board> findByOrgIdAndOwnerUserIdOrderByNameAsc(
            Long orgId,
            Long ownerUserId
    );

    List<Board> findByOrgIdAndStatusOrderByNameAsc(
            Long orgId,
            String status
    );

    List<Board> findByOrgIdAndProjectIdAndStatusOrderByNameAsc(
            Long orgId,
            Long projectId,
            String status
    );
}
