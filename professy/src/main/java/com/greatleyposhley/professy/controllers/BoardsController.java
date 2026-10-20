package com.greatleyposhley.professy.controllers;

import com.greatleyposhley.professy.entities.Board;
import com.greatleyposhley.professy.entities.UserAccount;
import com.greatleyposhley.professy.services.BoardService;
import com.greatleyposhley.professy.repositories.UserAccountRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@CrossOrigin
public class BoardsController {

    private final BoardService boardService;
    private final UserAccountRepository userAccountRepository;

    public BoardsController(BoardService boardService,
                            UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
        this.boardService = boardService;
    }

    /**
     * Get all boards
     */
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards(Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.getAllBoards(orgId)
        );
    }

    /**
     * Get board by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id,
                                              Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.getBoardById(orgId,
                        id)
        );
    }

    /**
     * Get boards by project
     *
     * GET /api/boards/project/10
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Board>> getBoardsByProject(@PathVariable Long projectId,
                                                          Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.getBoardsByProject(orgId,
                        projectId)
        );
    }

    /**
     * Get boards by owner
     *
     * GET /api/boards/owner/25
     */
    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<List<Board>> getBoardsByOwner(@PathVariable Long ownerUserId,
                                                        Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.getBoardsByOwner(orgId,
                        ownerUserId)
        );
    }

    /**
     * Get boards by status
     *
     * GET /api/boards/status/ACTIVE
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Board>> getBoardsByStatus(@PathVariable String status,
                                                         Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.getBoardsByStatus(orgId,
                        status)
        );
    }

    /**
     * Get boards by project and status
     *
     * GET /api/boards/project/10/status/ACTIVE
     */
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<Board>> getBoardsByProjectAndStatus(@PathVariable Long projectId,
                                                                   @PathVariable String status,
                                                                   Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.getBoardsByProjectAndStatus(
                        orgId,
                        projectId,
                        status
                )
        );
    }

    /**
     * Create board
     */
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board,
                                             Authentication authentication) {
        Long orgId = getOrgId(authentication);

        Board created = boardService.createBoard(orgId,
                board);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /**
     * Update board
     */
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id,
                                             @RequestBody Board board,
                                             Authentication authentication) {
        Long orgId = getOrgId(authentication);

        return ResponseEntity.ok(
                boardService.updateBoard(orgId,
                        id, board)
        );
    }

    /**
     * Delete board
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id,
                                            Authentication authentication) {
        Long orgId = getOrgId(authentication);

        boardService.deleteBoard(orgId,
                id);

        return ResponseEntity.noContent().build();
    }


    private Long getOrgId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication required");
        }

        String email = authentication.getName();

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        return userAccountRepository
                .findByEmail(email.trim().toLowerCase())
                .map(UserAccount::getOrgId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found")
                );
    }
}