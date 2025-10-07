CREATE TABLE refresh_tokens (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                usuario_id BIGINT NOT NULL,
                                token VARCHAR(512) NOT NULL UNIQUE,
                                expira_en TIMESTAMP NOT NULL,
                                revocado TINYINT(1) NOT NULL DEFAULT 0,
                                creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_rt_user FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
                                INDEX idx_rt_usuario (usuario_id),
                                INDEX idx_rt_expira (expira_en)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE password_reset_tokens (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       usuario_id BIGINT NOT NULL,
                                       token VARCHAR(256) NOT NULL UNIQUE,
                                       expira_en TIMESTAMP NOT NULL,
                                       usado TINYINT(1) NOT NULL DEFAULT 0,
                                       creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_pr_user FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
                                       INDEX idx_pr_usuario (usuario_id),
                                       INDEX idx_pr_expira (expira_en)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
