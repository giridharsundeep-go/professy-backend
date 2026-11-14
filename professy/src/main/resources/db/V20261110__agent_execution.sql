CREATE TABLE IF NOT EXISTS agent_execution_defaults (
 agent_id BIGINT PRIMARY KEY, org_id BIGINT NOT NULL, defaults_json JSON NOT NULL,
 updated_by BIGINT NOT NULL, updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
CREATE TABLE IF NOT EXISTS delivery_worker_reports (
 agent_id BIGINT PRIMARY KEY, org_id BIGINT NOT NULL, worker_id VARCHAR(100) NOT NULL,
 report_json JSON NOT NULL, reported_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);
CREATE TABLE IF NOT EXISTS delivery_job_events (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, org_id BIGINT NOT NULL, job_id CHAR(32) NOT NULL,
 worker_sequence BIGINT NOT NULL, event VARCHAR(80) NOT NULL, message TEXT NOT NULL,
 occurred_at VARCHAR(60) NOT NULL, received_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
 UNIQUE KEY event_retry(job_id,worker_sequence), INDEX job_timeline(org_id,job_id,id)
);
