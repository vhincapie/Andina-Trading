CREATE TABLE IF NOT EXISTS inversionistas (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              usuario_id BIGINT NOT NULL UNIQUE,
                                              correo VARCHAR(180) NOT NULL UNIQUE,

    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(30) NOT NULL UNIQUE,

    nombre VARCHAR(120) NOT NULL,
    apellido VARCHAR(120) NOT NULL,
    fecha_nacimiento DATE NOT NULL,

    pais_id BIGINT NOT NULL,
    ciudad_id BIGINT NOT NULL,

    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS alpaca_account (
                                              id_alpaca_account BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              alpaca_id VARCHAR(64) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    id_inversionista BIGINT NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_alpaca_inversionista
    FOREIGN KEY (id_inversionista) REFERENCES inversionistas(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;