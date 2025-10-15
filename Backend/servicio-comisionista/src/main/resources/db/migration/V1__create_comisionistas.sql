CREATE TABLE IF NOT EXISTS comisionistas (
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
    anios_experiencia INT NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

