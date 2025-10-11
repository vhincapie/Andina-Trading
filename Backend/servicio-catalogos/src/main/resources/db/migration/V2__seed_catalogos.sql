INSERT INTO situaciones_economicas (nombre, descripcion, estado) VALUES
                                                                     ('Crecimiento Moderado', 'PIB creciendo de forma sostenida', 'ACTIVO'),
                                                                     ('Estabilidad', 'Indicadores macroeconómicos estables', 'ACTIVO'),
                                                                     ('Inflación Alta', 'Presión inflacionaria notable', 'ACTIVO'),
                                                                     ('Recuperación', 'Recuperación económica pospandemia', 'ACTIVO');

INSERT INTO paises (codigo_iso3, nombre, estado, situacion_economica_id) VALUES
                                                                             ('ECU', 'Ecuador',   'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Crecimiento Moderado')),
                                                                             ('PER', 'Perú',      'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Estabilidad')),
                                                                             ('VEN', 'Venezuela', 'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Inflación Alta')),
                                                                             ('COL', 'Colombia',  'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Recuperación'));

INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Quito', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'ECU';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Guayaquil', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'ECU';

INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Lima', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'PER';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Arequipa', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'PER';

INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Caracas', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'VEN';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Maracaibo', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'VEN';

INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Bogotá', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'COL';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Medellín', id, 'ACTIVO' FROM paises WHERE codigo_iso3 = 'COL';
