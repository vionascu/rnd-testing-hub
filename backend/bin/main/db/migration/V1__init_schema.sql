-- Initial schema for rnd-testing-hub

-- Test suite runs table
CREATE TABLE IF NOT EXISTS test_suite_run (
    id BIGSERIAL PRIMARY KEY,
    suite_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_tests INT NOT NULL DEFAULT 0,
    passed_tests INT NOT NULL DEFAULT 0,
    failed_tests INT NOT NULL DEFAULT 0,
    skipped_tests INT NOT NULL DEFAULT 0,
    duration_ms BIGINT NOT NULL DEFAULT 0,
    timestamp TIMESTAMP NOT NULL,
    upload_source_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_test_suite_run_suite_name ON test_suite_run(suite_name);
CREATE INDEX idx_test_suite_run_timestamp ON test_suite_run(timestamp);

-- Test case runs table
CREATE TABLE IF NOT EXISTS test_case_run (
    id BIGSERIAL PRIMARY KEY,
    suite_run_id BIGINT NOT NULL,
    test_name VARCHAR(512) NOT NULL,
    status VARCHAR(20) NOT NULL,
    duration_ms BIGINT NOT NULL DEFAULT 0,
    error_message TEXT,
    timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (suite_run_id) REFERENCES test_suite_run(id) ON DELETE CASCADE
);

CREATE INDEX idx_test_case_run_test_name ON test_case_run(test_name);
CREATE INDEX idx_test_case_run_suite_run_id ON test_case_run(suite_run_id);
CREATE INDEX idx_test_case_run_status ON test_case_run(status);

-- API specs table
CREATE TABLE IF NOT EXISTS api_spec (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    version VARCHAR(50),
    spec_content TEXT NOT NULL,
    spec_format VARCHAR(20) NOT NULL,
    upload_source_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_spec_name ON api_spec(name);

-- API endpoints table
CREATE TABLE IF NOT EXISTS api_endpoint (
    id BIGSERIAL PRIMARY KEY,
    spec_id BIGINT NOT NULL,
    method VARCHAR(20) NOT NULL,
    path VARCHAR(512) NOT NULL,
    summary VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (spec_id) REFERENCES api_spec(id) ON DELETE CASCADE
);

CREATE INDEX idx_api_endpoint_spec_id ON api_endpoint(spec_id);
CREATE INDEX idx_api_endpoint_method_path ON api_endpoint(method, path);

-- Best practices table
CREATE TABLE IF NOT EXISTS practice (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    tags VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_practice_slug ON practice(slug);
CREATE INDEX idx_practice_tags ON practice(tags);

-- Lab sessions table
CREATE TABLE IF NOT EXISTS lab_session (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    current_step INT NOT NULL DEFAULT 1,
    openapi_spec_id BIGINT,
    junit_report_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lab_session_session_id ON lab_session(session_id);
