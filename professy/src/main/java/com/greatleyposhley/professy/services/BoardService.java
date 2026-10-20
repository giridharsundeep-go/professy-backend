package com.greatleyposhley.professy.services;

import com.greatleyposhley.professy.entities.Board;
import com.greatleyposhley.professy.repositories.BoardRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<Board> getAllBoards(Long orgId) {
        return boardRepository.findByOrgId(orgId);
    }


    @Transactional(readOnly = true)
    public Board getBoardById(Long orgId, Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Board not found with id: " + id)
                );
    }


    @Transactional(readOnly = true)
    public List<Board> getBoardsByProject(Long orgId, Long projectId) {
        return boardRepository.findByOrgIdAndProjectIdOrderByNameAsc(orgId, projectId);
    }


    @Transactional(readOnly = true)
    public List<Board> getBoardsByOwner(Long orgId, Long ownerUserId) {
        return boardRepository.findByOrgIdAndOwnerUserIdOrderByNameAsc(orgId, ownerUserId);
    }


    @Transactional(readOnly = true)
    public List<Board> getBoardsByStatus(Long orgId, String status) {
        return boardRepository.findByOrgIdAndStatusOrderByNameAsc(orgId, status);
    }


    @Transactional(readOnly = true)
    public List<Board> getBoardsByProjectAndStatus(Long orgId, Long projectId,
                                                   String status) {
        return boardRepository
                .findByOrgIdAndProjectIdAndStatusOrderByNameAsc(orgId, projectId, status);
    }


    public Board createBoard(Long orgId, Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Board is required");
        }
        board.setOrgId(orgId);

        if (board.getProjectId() == null) {
            throw new IllegalArgumentException(
                    "Project ID is required"
            );
        }

        if (board.getName() == null ||
                board.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Board name is required"
            );
        }

        boolean exists = boardRepository
                .existsByOrgIdAndProjectIdAndName(
                        orgId,
                        board.getProjectId(),
                        board.getName().trim()
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Board with name '" +
                            board.getName() +
                            "' already exists in this project"
            );
        }

        board.setName(board.getName().trim());

        if (board.getStatus() == null ||
                board.getStatus().trim().isEmpty()) {
            board.setStatus("ACTIVE");
        }

        return boardRepository.save(board);
    }


    public Board updateBoard(Long orgId, Long id, Board board) {
        if (board.getOrgId() != null && !orgId.equals(board.getOrgId())) {
            throw new IllegalArgumentException("Board belongs to a different organization");
        }

        Board existing = getBoardById(orgId, id);

        if (board.getProjectId() == null) {
            throw new IllegalArgumentException(
                    "Project ID is required"
            );
        }

        if (board.getName() == null ||
                board.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Board name is required"
            );
        }

        boolean duplicate = boardRepository
                .existsByOrgIdAndProjectIdAndNameAndIdNot(orgId,
                        board.getProjectId(),
                        board.getName().trim(),
                        id
                );

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Board with name '" +
                            board.getName() +
                            "' already exists in this project"
            );
        }

        existing.setProjectId(board.getProjectId());
        existing.setName(board.getName().trim());

        if (board.getOwnerUserId() != null) {
            existing.setOwnerUserId(board.getOwnerUserId());
        }

        if (board.getStatus() != null &&
                !board.getStatus().trim().isEmpty()) {
            existing.setStatus(board.getStatus().trim());
        }

        return boardRepository.save(existing);
    }


    public void deleteBoard(Long orgId, Long id) {

        if (!boardRepository.existsById(id)) {
            throw new RuntimeException(
                    "Board not found with id: " + id
            );
        }

        boardRepository.deleteById(id);
    }
}