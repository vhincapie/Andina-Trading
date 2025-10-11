CREATE TABLE IF NOT EXISTS situaciones_economicas (
                                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      nombre VARCHAR(150) NOT NULL UNIQUE,
    descripcion VARCHAR(400),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS paises (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      codigo_iso3 VARCHAR(3) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    situacion_economica_id BIGINT,
    CONSTRAINT uk_pais_iso3 UNIQUE (codigo_iso3),
    CONSTRAINT uk_pais_nombre UNIQUE (nombre),
    CONSTRAINT fk_pais_sit_eco FOREIGN KEY (situacion_economica_id) REFERENCES situaciones_economicas(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ciudades (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        nombre VARCHAR(150) NOT NULL UNIQUE,
    pais_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT fk_ciudad_pais FOREIGN KEY (pais_id) REFERENCES paises(id),
    CONSTRAINT uk_ciudad_pais_nombre UNIQUE (pais_id, nombre)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;