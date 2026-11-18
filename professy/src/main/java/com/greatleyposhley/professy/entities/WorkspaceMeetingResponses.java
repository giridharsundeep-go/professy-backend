package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_responses")
@IdClass(WorkspaceMeetingResponses.Key.class)
@Entity
public class WorkspaceMeetingResponses {
    @Id
    @Column(name = "meeting_id", nullable = false, columnDefinition = "BIGINT")
    private Long meetingId;
    @Id
    @Column(name = "participant_id", nullable = false, columnDefinition = "BIGINT")
    private Long participantId;
    @Column(name = "response", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String response;

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

    protected WorkspaceMeetingResponses() {}
}
