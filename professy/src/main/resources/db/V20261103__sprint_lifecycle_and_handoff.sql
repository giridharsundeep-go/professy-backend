CREATE TABLE IF NOT EXISTS sprint_lifecycle_settings (
 sprint_id BIGINT PRIMARY KEY,time_zone VARCHAR(80) NOT NULL DEFAULT 'Asia/Kolkata',
 updated_by BIGINT NULL,updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);
CREATE TABLE IF NOT EXISTS sprint_lifecycle_events (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,org_id BIGINT NOT NULL,project_id BIGINT NOT NULL,sprint_id BIGINT NOT NULL,
 actor_id BIGINT NOT NULL,event_type VARCHAR(40) NOT NULL,details JSON NOT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),KEY idx_sprint_lifecycle(sprint_id,id)
);
CREATE TABLE IF NOT EXISTS account_handoff_codes (
 code_hash CHAR(64) PRIMARY KEY,user_id BIGINT NOT NULL,credential_version INT NOT NULL,
 expires_at TIMESTAMP(6) NOT NULL,used_at TIMESTAMP(6) NULL,KEY idx_handoff_expiry(expires_at)
);
INSERT IGNORE INTO workspace_schema_migrations(version) VALUES('20261103_sprint_lifecycle_handoff');
