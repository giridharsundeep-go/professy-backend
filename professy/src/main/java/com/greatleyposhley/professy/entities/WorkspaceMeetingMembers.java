package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_members")
@IdClass(WorkspaceMeetingMembers.Key.class)
@Entity
public class WorkspaceMeetingMembers {
    @Id
    @Column(name = "meeting_id", nullable = false, columnDefinition = "BIGINT")
    private Long meetingId;
    @Id
    @Column(name = "participant_id", nullable = false, columnDefinition = "BIGINT")
    private Long participantId;
    @Column(name = "last_seen", nullable = true, columnDefinition = "TIMESTAMP(6)")
    private java.time.LocalDateTime lastSeen;

    public static class Key implements Serializable {
        public Long meetingId;
        public Long participantId;
        public Key() {}
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Key that)) return false;
            return Objects.equals(meetingId, that.meetingId) && Objects.equals(participantId, that.participantId);
        }
        @Override public int hashCode() { return Objects.hash(meetingId, participantId); }
    }

    protected WorkspaceMeetingMembers() {}
}
