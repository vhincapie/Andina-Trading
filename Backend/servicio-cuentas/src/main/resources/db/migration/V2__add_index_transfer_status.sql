ALTER TABLE transfer_log
    ADD INDEX idx_transfer_log_status_created (status, created_at);
