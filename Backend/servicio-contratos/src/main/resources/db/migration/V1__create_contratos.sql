CREATE TABLE IF NOT EXISTS contratos (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           inversionista_id BIGINT NOT NULL,
                           comisionista_id BIGINT NOT NULL,

                           estado VARCHAR(20) NOT NULL,
                           moneda VARCHAR(3) NOT NULL,

                           porcentaje_cobro_aplicado DECIMAL(5,2) NOT NULL,
                           terminos_texto TEXT NULL,

                           observaciones TEXT NULL,

                           fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           fecha_fin TIMESTAMP NULL,

                           creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

