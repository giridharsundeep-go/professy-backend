package com.greatleyposhley.professy.collaboration;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

/** Meeting dates are persisted in UTC; old unannotated records remain local to the legacy zone. */
final class MeetingSchedule {
 record Dates(LocalDateTime start, LocalDateTime end, String zone) {}
 static Dates parse(Map<String,Object> body) {
  String name=Objects.toString(body.get("timeZone"),"Asia/Kolkata");
  ZoneId zone=ZoneId.of(name);
  LocalDateTime start=date(body.get("startsAt"),zone),end=date(body.get("endsAt"),zone);
  if(start==null&&end!=null)throw new IllegalArgumentException("Set a start time before an end time");
  if(start!=null&&end==null)end=start.plusHours(1);
  if(start!=null&&!end.isAfter(start))throw new IllegalArgumentException("End must be after start");
  return new Dates(start,end,name);
 }
 private static LocalDateTime date(Object value,ZoneId zone) {
  if(value==null||value.toString().isBlank())return null;
  String text=value.toString();
  try{return OffsetDateTime.parse(text).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();}
  catch(DateTimeParseException ignored) {
   LocalDateTime local=LocalDateTime.parse(text);
   if(zone.getRules().getValidOffsets(local).size()!=1)throw new IllegalArgumentException("This local time is ambiguous or unavailable; choose a time with a UTC offset");
   return LocalDateTime.ofInstant(local.atZone(zone).toInstant(),ZoneOffset.UTC);
  }
 }
 static String instant(Object value,boolean utc,String legacyZone) {
  if(value==null)return null;
  LocalDateTime date=value instanceof java.sql.Timestamp t?t.toLocalDateTime():value instanceof LocalDateTime t?t:LocalDateTime.parse(value.toString().replace(' ','T'));
  return date.atZone(utc?ZoneOffset.UTC:ZoneId.of(legacyZone)).toInstant().toString();
 }
}
