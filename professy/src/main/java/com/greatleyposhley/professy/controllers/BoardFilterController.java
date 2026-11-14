package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.BoardFilter;
import com.greatleyposhley.professy.repositories.BoardFilterRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-filters")
public class BoardFilterController {

    private final BoardFilterRepository boardFilterRepository;
    private final com.greatleyposhley.professy.collaboration.WorkAccess access;
    private void check(Long id,boolean edit){var actor=access.actor();access.require(edit?"BOARDS.UPDATE":"BOARDS.READ");var rows=access.jdbc.queryForList("SELECT project_id FROM boards WHERE id=? AND org_id=?",Long.class,id,actor.getOrgId());if(rows.isEmpty())throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND,"Board not found");access.project(rows.getFirst());}

    public BoardFilterController(BoardFilterRepository boardFilterRepository, com.greatleyposhley.professy.collaboration.WorkAccess access) {
        this.access=access;
        this.boardFilterRepository = boardFilterRepository;
    }

    /**
     * Get all filters for a board
     *
     * GET /api/board-filters/board/1
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<BoardFilter>> getFiltersByBoard(
            @PathVariable Long boardId
    ) {
        check(boardId,false);
        return ResponseEntity.ok(
                boardFilterRepository.findByBoardId(boardId)
        );
    }

    /**
     * Get filters of a specific type
     *
     * GET /api/board-filters/board/1/type/TEAM
     */
    @GetMapping("/board/{boardId}/type/{filterType}")
    public ResponseEntity<List<BoardFilter>> getFiltersByType(
            @PathVariable Long boardId,
            @PathVariable String filterType
    ) {
        check(boardId,false);
        return ResponseEntity.ok(
                boardFilterRepository.findByBoardIdAndFilterType(
                        boardId,
                        filterType
                )
        );
    }

    /**
     * Create a filter
     *
     * POST /api/board-filters
     */
    @PostMapping
    public ResponseEntity<BoardFilter> createFilter(
            @RequestBody BoardFilter boardFilter
    ) {

        check(boardFilter.getBoardId(),true);
        BoardFilter saved =
                boardFilterRepository.save(boardFilter);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    /**
     * Update a filter
     *
     * PUT /api/board-filters/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardFilter> updateFilter(
            @PathVariable Long id,
            @RequestBody BoardFilter request
    ) {

        BoardFilter existing =
                boardFilterRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Board filter not found: " + id
                                )
                        );

        check(existing.getBoardId(),true);check(request.getBoardId(),true);
        existing.setBoardId(request.getBoardId());
        existing.setFilterType(request.getFilterType());
        existing.setFilterValue(request.getFilterValue());

        return ResponseEntity.ok(
                boardFilterRepository.save(existing)
        );
    }

    /**
     * Delete a single filter
     *
     * DELETE /api/board-filters/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilter(
            @PathVariable Long id
    ) {

        if (!boardFilterRepository.existsById(id)) {
            throw new RuntimeException(
                    "Board filter not found: " + id
            );
        }

        check(boardFilterRepository.findById(id).orElseThrow().getBoardId(),true);
        boardFilterRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all filters for a board
     *
     * DELETE /api/board-filters/board/1
     */
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<Void> deleteBoardFilters(
            @PathVariable Long boardId
    ) {

        check(boardId,true);
        boardFilterRepository.deleteByBoardId(boardId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all filters of a specific type
     *
     * DELETE /api/board-filters/board/1/type/TEAM
     */
    @DeleteMapping("/board/{boardId}/type/{filterType}")
    public ResponseEntity<Void> deleteFiltersByType(
            @PathVariable Long boardId,
            @PathVariable String filterType
    ) {

        check(boardId,true);
        boardFilterRepository.deleteByBoardIdAndFilterType(
                boardId,
                filterType
        );

        return ResponseEntity.noContent().build();
    }
    @PutMapping("/board/{boardId}") @org.springframework.transaction.annotation.Transactional
    public Object replace(@PathVariable Long boardId,@RequestBody List<BoardFilter> filters){
        check(boardId,true);if(filters.size()>10)throw new IllegalArgumentException("Too many board filters");
        for(var filter:filters){if(!java.util.Set.of("TEAM","SPRINT","PRIORITY","STATUS","TYPE").contains(filter.getFilterType())||filter.getFilterValue()==null||filter.getFilterValue().length()>255)throw new IllegalArgumentException("Invalid board filter");filter.setId(null);filter.setBoardId(boardId);}
        boardFilterRepository.deleteByBoardId(boardId);boardFilterRepository.flush();return boardFilterRepository.saveAll(filters);
    }
}