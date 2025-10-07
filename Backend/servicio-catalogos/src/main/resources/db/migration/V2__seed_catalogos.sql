INSERT INTO situaciones_economicas (nombre, descripcion, estado) VALUES
                                                                     ('Crecimiento Moderado', 'PIB creciendo de forma sostenida', 'ACTIVO'),
                                                                     ('Estabilidad', 'Indicadores macroeconómicos estables', 'ACTIVO'),
                                                                     ('Inflación Alta', 'Presión inflacionaria notable', 'ACTIVO'),
                                                                     ('Recuperación', 'Recuperación económica pospandemia', 'ACTIVO');


INSERT INTO paises (codigo_iso2, nombre, estado, situacion_economica_id) VALUES
                                                                             ('EC', 'Ecuador',   'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Crecimiento Moderado')),
                                                                             ('PE', 'Perú',      'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Estabilidad')),
                                                                             ('VE', 'Venezuela', 'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Inflación Alta')),
                                                                             ('CO', 'Colombia',  'ACTIVO', (SELECT id FROM situaciones_economicas WHERE nombre = 'Recuperación'));


-- Ecuador
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Quito', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'EC';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Guayaquil', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'EC';

-- Perú
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Lima', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'PE';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Arequipa', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'PE';

-- Venezuela
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Caracas', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'VE';
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Maracaibo', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'VE';

-- Colombia
INSERT INTO ciudades (nombre, pais_id, estado)
SELECT 'Bogotá', id, 'ACTIVO' FROM paises WHERE codigo_iso2 = 'CO';