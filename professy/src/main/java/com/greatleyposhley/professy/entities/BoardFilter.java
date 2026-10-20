package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;

@Entity
@Table(
        name = "board_filters",
        indexes = {
                @Index(
                        name = "idx_board_filters_board_id",
                        columnList = "board_id"
                ),
                @Index(
                        name = "idx_board_filters_type",
                        columnList = "board_id, filter_type"
                )
        }
)
public class BoardFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "filter_type", nullable = false, length = 30)
    private String filterType;

    @Column(name = "filter_value", nullable = false, length = 255)
    private String filterValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
}