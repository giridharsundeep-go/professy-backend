CREATE TABLE IF NOT EXISTS workspace_actors(id BIGINT AUTO_INCREMENT PRIMARY KEY,org_id BIGINT NOT NULL,actor_type VARCHAR(10) NOT NULL,display_name VARCHAR(120) NOT NULL,user_id BIGINT NULL,provider VARCHAR(100) NULL,status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',created_by BIGINT NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),KEY ix_actor_org(org_id,actor_type));
CREATE TABLE IF NOT EXISTS workspace_agent_profiles(actor_id BIGINT PRIMARY KEY,designation VARCHAR(120) NOT NULL DEFAULT '',description TEXT NOT NULL,instructions TEXT NOT NULL,model VARCHAR(120) NOT NULL DEFAULT '',FOREIGN KEY(actor_id) REFERENCES workspace_actors(id));
CREATE TABLE IF NOT EXISTS workspace_agent_projects(actor_id BIGINT NOT NULL,project_id BIGINT NOT NULL,PRIMARY KEY(actor_id,project_id),FOREIGN KEY(actor_id) REFERENCES workspace_actors(id));
CREATE TABLE IF NOT EXISTS agent_workspace_roles(actor_id BIGINT NOT NULL,role_code VARCHAR(50) NOT NULL,PRIMARY KEY(actor_id,role_code),FOREIGN KEY(actor_id) REFERENCES workspace_actors(id));
CREATE TABLE IF NOT EXISTS agent_permission_overrides(actor_id BIGINT NOT NULL,permission VARCHAR(80) NOT NULL,allowed BOOLEAN NOT NULL,PRIMARY KEY(actor_id,permission),FOREIGN KEY(actor_id) REFERENCES workspace_actors(id));
CREATE TABLE IF NOT EXISTS ticket_actor_assignments(org_id BIGINT NOT NULL,project_id BIGINT NOT NULL,ticket_type VARCHAR(20) NOT NULL,ticket_id BIGINT NOT NULL,assignment_role VARCHAR(20) NOT NULL,actor_id BIGINT NOT NULL,updated_by BIGINT NOT NULL,updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),PRIMARY KEY(org_id,ticket_type,ticket_id,assignment_role),KEY ix_actor_work(actor_id,project_id),FOREIGN KEY(actor_id) REFERENCES workspace_actors(id));
CREATE TABLE IF NOT EXISTS workspace_files(id BIGINT AUTO_INCREMENT PRIMARY KEY,org_id BIGINT NOT NULL,file_type VARCHAR(20) NOT NULL,title VARCHAR(255) NOT NULL,owner_id BIGINT NOT NULL,content_json MEDIUMTEXT NOT NULL,tags_json TEXT NOT NULL,revision BIGINT NOT NULL DEFAULT 1,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),KEY ix_files_owner(org_id,owner_id,file_type));
CREATE TABLE IF NOT EXISTS workspace_file_shares(file_id BIGINT NOT NULL,user_id BIGINT NOT NULL,permission VARCHAR(10) NOT NULL,PRIMARY KEY(file_id,user_id),FOREIGN KEY(file_id) REFERENCES workspace_files(id) ON DELETE CASCADE);
CREATE TABLE IF NOT EXISTS workspace_file_versions(file_id BIGINT NOT NULL,revision BIGINT NOT NULL,title VARCHAR(255) NOT NULL,content_json MEDIUMTEXT NOT NULL,saved_by BIGINT NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),PRIMARY KEY(file_id,revision),FOREIGN KEY(file_id) REFERENCES workspace_files(id) ON DELETE CASCADE);
CREATE TABLE IF NOT EXISTS workspace_file_comments(id BIGINT AUTO_INCREMENT PRIMARY KEY,file_id BIGINT NOT NULL,user_id BIGINT NOT NULL,content TEXT NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),FOREIGN KEY(file_id) REFERENCES workspace_files(id) ON DELETE CASCADE);
CREATE TABLE IF NOT EXISTS workspace_meetings(id BIGINT AUTO_INCREMENT PRIMARY KEY,org_id BIGINT NOT NULL,title VARCHAR(255) NOT NULL,owner_id BIGINT NOT NULL,starts_at DATETIME NULL,ends_at DATETIME NULL,status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),KEY ix_meetings(org_id,owner_id));
CREATE TABLE IF NOT EXISTS workspace_meeting_members(meeting_id BIGINT NOT NULL,participant_id BIGINT NOT NULL,last_seen TIMESTAMP(6) NULL,PRIMARY KEY(meeting_id,participant_id),FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE);
CREATE TABLE IF NOT EXISTS workspace_meeting_messages(id BIGINT AUTO_INCREMENT PRIMARY KEY,meeting_id BIGINT NOT NULL,author_id BIGINT NOT NULL,body TEXT NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE);
CREATE TABLE IF NOT EXISTS workspace_meeting_signals(id BIGINT AUTO_INCREMENT PRIMARY KEY,meeting_id BIGINT NOT NULL,sender_id BIGINT NOT NULL,recipient_id BIGINT NOT NULL,payload TEXT NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),KEY ix_signal_delivery(meeting_id,recipient_id,id),FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE);
UPDATE document_sharing SET general_access='RESTRICTED' WHERE general_access<>'RESTRICTED';

CREATE TABLE IF NOT EXISTS workspace_meeting_agent_sessions(token_hash CHAR(64) PRIMARY KEY,meeting_id BIGINT NOT NULL,agent_id BIGINT NOT NULL,org_id BIGINT NOT NULL,created_by BIGINT NOT NULL,expires_at TIMESTAMP(6) NOT NULL,created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),KEY ix_meeting_agent_session(meeting_id,agent_id),FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE,FOREIGN KEY(agent_id) REFERENCES workspace_actors(id));

CREATE TABLE IF NOT EXISTS workspace_meeting_presence(meeting_id BIGINT NOT NULL,participant_id BIGINT NOT NULL,audio_enabled BOOLEAN NOT NULL DEFAULT FALSE,video_enabled BOOLEAN NOT NULL DEFAULT FALSE,screen_sharing BOOLEAN NOT NULL DEFAULT FALSE,hand_raised BOOLEAN NOT NULL DEFAULT FALSE,PRIMARY KEY(meeting_id,participant_id),FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE);

-- Gather is the authoritative calendar record for meetings. Legacy rows retain local time.
CREATE TABLE IF NOT EXISTS workspace_meeting_schedule (
 meeting_id BIGINT PRIMARY KEY, time_zone VARCHAR(80) NOT NULL,
 description TEXT NOT NULL, revision BIGINT NOT NULL DEFAULT 1,
 CONSTRAINT fk_meeting_schedule FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS workspace_meeting_responses (
 meeting_id BIGINT NOT NULL, participant_id BIGINT NOT NULL,
 response VARCHAR(20) NOT NULL DEFAULT 'PENDING', PRIMARY KEY(meeting_id,participant_id),
 CONSTRAINT fk_meeting_response FOREIGN KEY(meeting_id) REFERENCES workspace_meetings(id) ON DELETE CASCADE
);
