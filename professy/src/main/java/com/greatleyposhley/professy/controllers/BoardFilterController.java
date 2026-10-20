package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.BoardFilter;
import com.greatleyposhley.professy.repositories.BoardFilterRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-filters")
@CrossOrigin
public class BoardFilterController {

    private final BoardFilterRepository boardFilterRepository;

    public BoardFilterController(BoardFilterRepository boardFilterRepository) {
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

        boardFilterRepository.deleteByBoardIdAndFilterType(
                boardId,
                filterType
        );

        return ResponseEntity.noContent().build();
    }
}