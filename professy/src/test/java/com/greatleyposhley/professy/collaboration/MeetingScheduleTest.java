package com.greatleyposhley.professy.collaboration;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class MeetingScheduleTest {
 @Test void equivalentOffsetsAndLocalTimesPersistTheSameInstant(){
  var local=MeetingSchedule.parse(Map.of("startsAt","2026-11-12T10:00","timeZone","Asia/Kolkata"));
  var offset=MeetingSchedule.parse(Map.of("startsAt","2026-11-12T10:00:00+05:30","timeZone","Asia/Kolkata"));
  assertEquals(LocalDateTime.parse("2026-11-12T04:30"),local.start());
  assertEquals(local,offset);assertEquals(local.start().plusHours(1),local.end());
 }
 @Test void invalidEndAndAmbiguousLocalTimesAreRejected(){
  assertThrows(IllegalArgumentException.class,()->MeetingSchedule.parse(Map.of("endsAt","2026-11-12T10:00")));
  assertThrows(IllegalArgumentException.class,()->MeetingSchedule.parse(Map.of("startsAt","2026-11-12T10:00","endsAt","2026-11-12T09:00")));
  assertThrows(IllegalArgumentException.class,()->MeetingSchedule.parse(Map.of("startsAt","2026-11-01T01:30","timeZone","America/New_York")));
  assertThrows(IllegalArgumentException.class,()->MeetingSchedule.parse(Map.of("startsAt","2026-03-08T02:30","timeZone","America/New_York")));
 }
 @Test void legacyAndNewRecordsDisplayWithoutTimezoneDrift(){
  var stamp=java.sql.Timestamp.valueOf("2026-11-12 10:00:00");
  assertEquals("2026-11-12T04:30:00Z",MeetingSchedule.instant(stamp,false,"Asia/Kolkata"));
  assertEquals("2026-11-12T10:00:00Z",MeetingSchedule.instant(stamp,true,"Asia/Kolkata"));
 }
}
