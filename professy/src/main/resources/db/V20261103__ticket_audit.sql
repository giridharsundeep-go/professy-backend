-- Database-level transactional outbox: all ticket writes, including bulk SQL, are covered.
DELIMITER $$
CREATE PROCEDURE workspace_record_ticket_change(
 IN p_org BIGINT, IN p_project BIGINT, IN p_type VARCHAR(20), IN p_ticket BIGINT,
 IN p_actor BIGINT, IN p_action VARCHAR(50), IN p_before JSON, IN p_after JSON)
BEGIN
 DECLARE event_uuid CHAR(36);
 IF p_project IS NOT NULL AND p_project > 0 THEN
  SET event_uuid=UUID();
  INSERT INTO ticket_activity(event_id,org_id,project_id,ticket_type,ticket_id,actor_id,event_type,summary,changes_json)
  VALUES(event_uuid,p_org,p_project,p_type,p_ticket,COALESCE(p_actor,0),p_action,
   CONCAT(REPLACE(p_action,'_',' '),': ',LEFT(COALESCE(JSON_UNQUOTE(JSON_EXTRACT(p_after,'$.title')),JSON_UNQUOTE(JSON_EXTRACT(p_before,'$.title')),CONCAT(p_type,'-',p_ticket)),250)),
   JSON_OBJECT('before',COALESCE(p_before,JSON_OBJECT()),'after',COALESCE(p_after,JSON_OBJECT())));
  INSERT INTO ticket_event_outbox(event_id,aggregate_key,payload)
  VALUES(event_uuid,CONCAT(p_org,':',p_type,':',p_ticket),JSON_OBJECT('eventId',event_uuid));
  INSERT IGNORE INTO ticket_watchers(org_id,project_id,ticket_type,ticket_id,user_id)
  SELECT p_org,p_project,p_type,p_ticket,u.id FROM user_account u WHERE u.org_id=p_org AND u.id IN
   (p_actor,JSON_VALUE(p_before,'$.assigneeId' RETURNING UNSIGNED NULL ON EMPTY NULL ON ERROR),
    JSON_VALUE(p_after,'$.assigneeId' RETURNING UNSIGNED NULL ON EMPTY NULL ON ERROR),
    JSON_VALUE(p_before,'$.reporterId' RETURNING UNSIGNED NULL ON EMPTY NULL ON ERROR),
    JSON_VALUE(p_after,'$.reporterId' RETURNING UNSIGNED NULL ON EMPTY NULL ON ERROR),
    JSON_VALUE(p_after,'$.creatorId' RETURNING UNSIGNED NULL ON EMPTY NULL ON ERROR));
 END IF;
END$$
DELIMITER ;
