package com.greatleyposhley.professy.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Table(name = "workspace_meeting_presence")
@IdClass(WorkspaceMeetingPresence.Key.class)
@Entity
public class WorkspaceMeetingPresence {
    @Id
    @Column(name = "meeting_id", nullable = false, columnDefinition = "BIGINT")
    private Long meetingId;
    @Id
    @Column(name = "participant_id", nullable = false, columnDefinition = "BIGINT")
    private Long participantId;
    @Column(name = "audio_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean audioEnabled;
    @Column(name = "video_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean videoEnabled;
    @Column(name = "screen_sharing", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean screenSharing;
    @Column(name = "hand_raised", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean handRaised;

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

    protected WorkspaceMeetingPresence() {}
}
