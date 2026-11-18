package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "retrospective_votes")
@IdClass(RetrospectiveVotes.Key.class)
@Entity
public class RetrospectiveVotes {
    @Id
    @Column(name = "entry_id", nullable = false, columnDefinition = "BIGINT")
    private Long entryId;
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT")
    private Long userId;

    public static class Key implements Serializable {
        public Long entryId;
        public Long userId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(entryId, that.entryId) && Objects.equals(userId, that.userId);
        }
        @Override public int hashCode() { return Objects.hash(entryId, userId); }
    }

    protected RetrospectiveVotes() {}
}
