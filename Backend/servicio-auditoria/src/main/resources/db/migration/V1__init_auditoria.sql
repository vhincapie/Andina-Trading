CREATE TABLE IF NOT EXISTS audit_logs (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          event_code VARCHAR(100) NOT NULL,
    user_id VARCHAR(100),
    username VARCHAR(150),
    ip_address VARCHAR(64),
    resource VARCHAR(200),
    action VARCHAR(200),
    details_json JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_created_at ON audit_logs (created_at);
CREATE INDEX idx_event_user ON audit_logs (event_code, user_id);
