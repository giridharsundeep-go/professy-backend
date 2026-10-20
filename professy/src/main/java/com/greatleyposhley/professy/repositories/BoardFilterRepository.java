package com.greatleyposhley.professy.repositories;

import com.greatleyposhley.professy.entities.BoardFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFilterRepository
        extends JpaRepository<BoardFilter, Long> {

    List<BoardFilter> findByBoardId(Long boardId);

    List<BoardFilter> findByBoardIdAndFilterType(
            Long boardId,
            String filterType
    );

    void deleteByBoardId(Long boardId);

    void deleteByBoardIdAndFilterType(
            Long boardId,
            String filterType
    );
}