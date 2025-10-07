CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       nombre VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          correo VARCHAR(180) NOT NULL UNIQUE,
                          contrasena_hash VARCHAR(255) NOT NULL,
                          estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
                          intentos_fallidos INT NOT NULL DEFAULT 0,
                          ultimo_login TIMESTAMP NULL,
                          rol_id BIGINT NOT NULL,
                          creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

INSERT INTO roles (nombre) VALUES ('ADMIN'),('COMISIONISTA'),('INVERSIONISTA');
