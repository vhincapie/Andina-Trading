CREATE TABLE IF NOT EXISTS account_ach_relationship (
                                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                        created_at DATETIME NULL,
                                                        ach_id VARCHAR(64) NOT NULL UNIQUE,
    account_owner_name VARCHAR(120),
    bank_account_type VARCHAR(30),
    bank_account_number VARCHAR(34),
    nickname VARCHAR(80),
    alpaca_account_id VARCHAR(64) NOT NULL,
    INDEX idx_ach_alpaca (alpaca_account_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS transfer_log (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            alpaca_account_id VARCHAR(64) NOT NULL,
    external_id VARCHAR(100),
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_transfer_log_acc_created
    ON transfer_log (alpaca_account_id, created_at);
