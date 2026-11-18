package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_file_comments")
@Entity
public class WorkspaceFileComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    @Column(name = "file_id", nullable = false, columnDefinition = "BIGINT")
    private Long fileId;
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private java.time.LocalDateTime createdAt;

    protected WorkspaceFileComments() {}
}
