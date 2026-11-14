package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class WorkspaceRealtimeTest {
 @Test void privateUpdatesAreVisibleOnlyToParticipants(){var event=Map.of("actorId",1L,"ownerId",2L,"recipients",List.of(3L,4L));for(long id:List.of(1L,2L,3L,4L))assertTrue(WorkspaceRealtime.recipient(event,id));assertFalse(WorkspaceRealtime.recipient(event,5));assertFalse(WorkspaceRealtime.recipient(Map.of(),5));}
}
