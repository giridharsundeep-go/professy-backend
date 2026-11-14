CREATE TABLE IF NOT EXISTS ticket_execution_specs (
 org_id BIGINT NOT NULL,ticket_kind VARCHAR(15) NOT NULL,ticket_id BIGINT NOT NULL,
 project_id BIGINT NOT NULL,spec_json JSON NOT NULL,revision BIGINT NOT NULL DEFAULT 1,
 updated_by BIGINT NOT NULL,updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 PRIMARY KEY(org_id,ticket_kind,ticket_id)
);
CREATE TABLE IF NOT EXISTS ticket_execution_context (
 job_id CHAR(32) PRIMARY KEY,context_json JSON NOT NULL
);
CREATE TABLE IF NOT EXISTS sprint_retrospectives (
 sprint_id BIGINT PRIMARY KEY,org_id BIGINT NOT NULL,project_id BIGINT NOT NULL,
 state VARCHAR(20) NOT NULL DEFAULT 'COLLECTING',summary TEXT NOT NULL,
 revision BIGINT NOT NULL DEFAULT 1,updated_by BIGINT NOT NULL,updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
CREATE TABLE IF NOT EXISTS retrospective_entries (
 id BIGINT AUTO_INCREMENT PRIMARY KEY,sprint_id BIGINT NOT NULL,org_id BIGINT NOT NULL,
 category VARCHAR(20) NOT NULL,body TEXT NOT NULL,author_id BIGINT NOT NULL,
 owner_id BIGINT NULL,due_date DATE NULL,action_issue_id BIGINT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),INDEX retro_scope(org_id,sprint_id)
);
CREATE TABLE IF NOT EXISTS retrospective_votes (
 entry_id BIGINT NOT NULL,user_id BIGINT NOT NULL,PRIMARY KEY(entry_id,user_id)
);

CREATE TABLE IF NOT EXISTS incident_ticket_links(org_id BIGINT NOT NULL,incident_id VARCHAR(36) NOT NULL,issue_id BIGINT NULL,PRIMARY KEY(org_id,incident_id));
