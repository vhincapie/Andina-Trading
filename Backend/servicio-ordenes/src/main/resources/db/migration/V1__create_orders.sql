CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                      inversionista_id BIGINT NOT NULL,
                                      comisionista_id BIGINT NOT NULL,

                                      alpaca_order_id VARCHAR(64) NULL,

    symbol VARCHAR(16) NOT NULL,
    qty DECIMAL(18,6) NOT NULL,

    order_type VARCHAR(20) NOT NULL,
    side VARCHAR(10) NOT NULL,
    time_in_force VARCHAR(20) NOT NULL,

    limit_price DECIMAL(18,6) NULL,
    stop_price DECIMAL(18,6) NULL,

    status VARCHAR(30) NOT NULL,

    unit_price DECIMAL(18,6) NOT NULL,
    transaction_amount DECIMAL(18,6) NOT NULL,
    commission_amount DECIMAL(18,6) NOT NULL,
    net_amount DECIMAL(18,6) NOT NULL,

    moneda VARCHAR(3) NOT NULL DEFAULT 'USD',

    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    approved_by BIGINT NULL,
    approved_at TIMESTAMP NULL,
    reject_reason VARCHAR(255) NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;