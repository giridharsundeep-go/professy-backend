CREATE TABLE IF NOT EXISTS delivery_services (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, org_id BIGINT NOT NULL, project_id BIGINT NOT NULL,
 name VARCHAR(160) NOT NULL, description TEXT NOT NULL, kind VARCHAR(30) NOT NULL,
 environment VARCHAR(40) NOT NULL, repository_url VARCHAR(500) NOT NULL DEFAULT '',
 owner_id BIGINT NULL, owner_type VARCHAR(10) NOT NULL DEFAULT 'HUMAN', port INT NULL,
 created_by BIGINT NOT NULL, updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 UNIQUE KEY service_name(org_id,project_id,name), INDEX services_project(org_id,project_id)
);
CREATE TABLE IF NOT EXISTS delivery_architectures (
 org_id BIGINT NOT NULL, project_id BIGINT NOT NULL, revision BIGINT NOT NULL DEFAULT 1,
 graph_json JSON NOT NULL, updated_by BIGINT NOT NULL, updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 PRIMARY KEY(org_id,project_id)
);
CREATE TABLE IF NOT EXISTS delivery_agent_config (
 agent_id BIGINT PRIMARY KEY, org_id BIGINT NOT NULL, enabled BOOLEAN NOT NULL DEFAULT FALSE,
 skills_json JSON NOT NULL, worker_hash CHAR(64) NULL, last_seen TIMESTAMP(6) NULL,
 model VARCHAR(120) NOT NULL, review_required BOOLEAN NOT NULL DEFAULT TRUE,
 updated_by BIGINT NOT NULL, updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
CREATE TABLE IF NOT EXISTS delivery_support (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, org_id BIGINT NOT NULL, project_id BIGINT NOT NULL,
 service_id BIGINT NOT NULL, issue_id BIGINT NOT NULL, requester_id BIGINT NOT NULL, agent_id BIGINT NULL,
 title VARCHAR(255) NOT NULL, description TEXT NOT NULL, priority VARCHAR(15) NOT NULL,
 state VARCHAR(25) NOT NULL DEFAULT 'OPEN', workflow VARCHAR(50) NOT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 INDEX support_scope(org_id,project_id,id)
);
CREATE TABLE IF NOT EXISTS delivery_jobs (
 id CHAR(32) PRIMARY KEY, org_id BIGINT NOT NULL, project_id BIGINT NOT NULL,
 support_id BIGINT NULL, ticket_kind VARCHAR(15) NOT NULL, ticket_id BIGINT NOT NULL,
 service_id BIGINT NOT NULL, agent_id BIGINT NOT NULL, requested_by BIGINT NOT NULL,
 title VARCHAR(255) NOT NULL, instructions TEXT NOT NULL, workflow VARCHAR(50) NOT NULL,
 state VARCHAR(25) NOT NULL DEFAULT 'QUEUED', lease_until TIMESTAMP(6) NULL, lease_token CHAR(64) NULL,
 worker_id VARCHAR(100) NULL, result_json JSON NULL, attachment_id BIGINT NULL,
 created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 INDEX jobs_queue(agent_id,state,created_at), INDEX jobs_ticket(org_id,ticket_kind,ticket_id)
);
CREATE TABLE IF NOT EXISTS delivery_release_services (
 release_id BIGINT NOT NULL, service_id BIGINT NOT NULL, job_id CHAR(32) NULL, org_id BIGINT NOT NULL,
 PRIMARY KEY(release_id,service_id), UNIQUE KEY job_release(job_id)
);
CREATE TABLE IF NOT EXISTS delivery_evidence (
 attachment_id BIGINT PRIMARY KEY, job_id CHAR(32) NOT NULL, agent_id BIGINT NOT NULL, org_id BIGINT NOT NULL
);
