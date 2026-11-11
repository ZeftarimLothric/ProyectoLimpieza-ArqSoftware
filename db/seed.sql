
-- Issue #6 - Datos de prueba
-- Sistema de Gestion de Actividades de Limpieza
-- Ejecutar despues de schema.sql y del catalogo SEPOMEX

USE gestion_limpieza;

-- Usuario administrador

SOURCE db/seed_admin.sql;

START TRANSACTION;

-- Estados de actividad

INSERT INTO estados_actividad (NOMBRE_ESTADO)
SELECT d.NOMBRE_ESTADO
FROM (
    SELECT 'PROGRAMADA' AS NOMBRE_ESTADO
    UNION ALL SELECT 'EN PROCESO'
    UNION ALL SELECT 'COMPLETADA'
) AS d
WHERE NOT EXISTS (
    SELECT 1
    FROM estados_actividad e
    WHERE e.NOMBRE_ESTADO = d.NOMBRE_ESTADO
);

-- Empleados

INSERT INTO empleados
(NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, TELEFONO, PUESTO, ACTIVO)
SELECT d.NOMBRE, d.PATERNO, d.MATERNO, d.TELEFONO, d.PUESTO, 1
FROM (
    SELECT 'Carlos' AS NOMBRE, 'Ramirez' AS PATERNO,
           'Lopez' AS MATERNO, '8441000001' AS TELEFONO,
           'Supervisor' AS PUESTO
    UNION ALL SELECT 'Ana', 'Martinez', 'Garcia', '8441000002', 'Operario'
    UNION ALL SELECT 'Rosa', 'Castilla', 'Espinoza', '8441000003', 'Operario'
    UNION ALL SELECT 'Maria', 'Gonzalez', 'Perez', '8441000004', 'Supervisor'
    UNION ALL SELECT 'Luis', 'Rodriguez', 'Sanchez', '8441000005', 'Operario'
    UNION ALL SELECT 'Sofia', 'Castillo', 'Flores', '8441000006', 'Operario'
    UNION ALL SELECT 'Pedro', 'Morales', 'Diaz', '8441000007', 'Supervisor'
    UNION ALL SELECT 'Daniela', 'Vargas', 'Reyes', '8441000008', 'Operario'
    UNION ALL SELECT 'Miguel', 'Cruz', 'Mendoza', '8441000009', 'Operario'
) AS d
WHERE NOT EXISTS (
    SELECT 1
    FROM empleados e
    WHERE e.TELEFONO = d.TELEFONO
);

-- Cuadrillas

INSERT INTO cuadrillas (NOMBRE, ACTIVO)
SELECT d.NOMBRE, 1
FROM (
    SELECT 'Cuadrilla Centro' AS NOMBRE
    UNION ALL SELECT 'Cuadrilla Norte'
    UNION ALL SELECT 'Cuadrilla Sur'
) AS d
WHERE NOT EXISTS (
    SELECT 1
    FROM cuadrillas c
    WHERE c.NOMBRE = d.NOMBRE
);

-- Asignacion de empleados
-- ES_JEFE: 1 = jefe, 0 = integrante

INSERT INTO cuadrilla_empleado
(ID_CUADRILLA, ID_EMPLEADO, FECHA_ASIGNACION, ES_JEFE)
SELECT
    c.ID_CUADRILLA,
    e.ID_EMPLEADO,
    CURRENT_DATE(),
    d.ES_JEFE
FROM (
    SELECT 'Cuadrilla Centro' AS CUADRILLA,
           '8441000001' AS TELEFONO, 1 AS ES_JEFE
    UNION ALL SELECT 'Cuadrilla Centro', '8441000002', 0
    UNION ALL SELECT 'Cuadrilla Centro', '8441000003', 0
    UNION ALL SELECT 'Cuadrilla Norte', '8441000004', 1
    UNION ALL SELECT 'Cuadrilla Norte', '8441000005', 0
    UNION ALL SELECT 'Cuadrilla Norte', '8441000006', 0
    UNION ALL SELECT 'Cuadrilla Sur', '8441000007', 1
    UNION ALL SELECT 'Cuadrilla Sur', '8441000008', 0
    UNION ALL SELECT 'Cuadrilla Sur', '8441000009', 0
) AS d
INNER JOIN (
    SELECT MIN(ID_CUADRILLA) AS ID_CUADRILLA, NOMBRE
    FROM cuadrillas
    GROUP BY NOMBRE
) AS c ON c.NOMBRE = d.CUADRILLA
INNER JOIN (
    SELECT MIN(ID_EMPLEADO) AS ID_EMPLEADO, TELEFONO
    FROM empleados
    GROUP BY TELEFONO
) AS e ON e.TELEFONO = d.TELEFONO
WHERE NOT EXISTS (
    SELECT 1
    FROM cuadrilla_empleado ce
    WHERE ce.ID_CUADRILLA = c.ID_CUADRILLA
      AND ce.ID_EMPLEADO = e.ID_EMPLEADO
);

-- Tipos de actividad

INSERT INTO tipo_actividad (NOMBRE, DESCRIPCION)
SELECT d.NOMBRE, d.DESCRIPCION
FROM (
    SELECT 'Barrido' AS NOMBRE,
           'Limpieza y barrido de calles y espacios publicos' AS DESCRIPCION
    UNION ALL
    SELECT 'Poda',
           'Poda de arboles y mantenimiento de areas verdes'
    UNION ALL
    SELECT 'Limpieza de baldios',
           'Retiro de basura y maleza en terrenos baldios'
    UNION ALL
    SELECT 'Retiro de escombro',
           'Recoleccion y retiro de escombro'
) AS d
WHERE NOT EXISTS (
    SELECT 1
    FROM tipo_actividad t
    WHERE t.NOMBRE = d.NOMBRE
);

-- Actividades de demostracion

DROP TEMPORARY TABLE IF EXISTS tmp_seed_actividades;

CREATE TEMPORARY TABLE tmp_seed_actividades (
    CODIGO INT PRIMARY KEY,
    TITULO VARCHAR(100) NOT NULL,
    DESCRIPCION VARCHAR(200) NOT NULL,
    TIPO VARCHAR(100) NOT NULL,
    ESTADO VARCHAR(50) NOT NULL,
    CUADRILLA VARCHAR(100) NOT NULL,
    COLONIA VARCHAR(100) NOT NULL,
    CODIGO_POSTAL VARCHAR(5) NOT NULL,
    LUGAR VARCHAR(200) NOT NULL,
    DIAS INT NOT NULL,
    HORA_INICIO TIME NOT NULL,
    HORA_FIN TIME NOT NULL
);

INSERT INTO tmp_seed_actividades VALUES
(1, '[DEMO #6] Barrido de plaza principal',
 'Limpieza y barrido de la zona centro',
 'Barrido', 'COMPLETADA', 'Cuadrilla Centro',
 'Saltillo Centro', '25000', 'Plaza principal',
 -7, '08:00:00', '11:00:00'),

(2, '[DEMO #6] Poda en zona centro',
 'Mantenimiento de arboles y areas verdes',
 'Poda', 'COMPLETADA', 'Cuadrilla Centro',
 'Saltillo Centro', '25000', 'Calles del centro',
 -5, '09:00:00', '12:00:00'),

(3, '[DEMO #6] Limpieza de terreno',
 'Retiro de maleza y residuos',
 'Limpieza de baldios', 'COMPLETADA', 'Cuadrilla Norte',
 'Vista Hermosa', '25010', 'Terreno baldio del sector',
 -3, '08:00:00', '13:00:00'),

(4, '[DEMO #6] Retiro de escombro norte',
 'Recoleccion de residuos de construccion',
 'Retiro de escombro', 'EN PROCESO', 'Cuadrilla Norte',
 'La Fuente', '25010', 'Vialidad principal',
 0, '08:00:00', '14:00:00'),

(5, '[DEMO #6] Barrido en Vista Hermosa',
 'Barrido de calles y banquetas',
 'Barrido', 'EN PROCESO', 'Cuadrilla Sur',
 'Vista Hermosa', '25010', 'Calles del sector',
 0, '09:00:00', '13:00:00'),

(6, '[DEMO #6] Poda en La Fuente',
 'Poda preventiva y limpieza de areas verdes',
 'Poda', 'PROGRAMADA', 'Cuadrilla Norte',
 'La Fuente', '25010', 'Area verde del sector',
 1, '08:00:00', '12:00:00'),

(7, '[DEMO #6] Limpieza de baldio sur',
 'Retiro de residuos y maleza acumulada',
 'Limpieza de baldios', 'PROGRAMADA', 'Cuadrilla Sur',
 'Privada la Misión', '25010', 'Terreno del sector',
 2, '09:00:00', '14:00:00'),

(8, '[DEMO #6] Barrido de vialidades',
 'Limpieza general de vialidades',
 'Barrido', 'PROGRAMADA', 'Cuadrilla Centro',
 'Privada la Campana', '25010', 'Vialidades interiores',
 3, '08:00:00', '11:00:00'),

(9, '[DEMO #6] Retiro de escombro',
 'Retiro de materiales y residuos',
 'Retiro de escombro', 'PROGRAMADA', 'Cuadrilla Sur',
 'Privada la Campana', '25010', 'Espacio publico',
 4, '10:00:00', '15:00:00'),

(10, '[DEMO #6] Poda y mantenimiento',
 'Mantenimiento de arbolado urbano',
 'Poda', 'PROGRAMADA', 'Cuadrilla Centro',
 'Privada la Misión', '25010', 'Area verde vecinal',
 6, '08:00:00', '12:00:00');

-- Insertar actividades con identificadores reales

INSERT INTO actividades
(
    ID_TIPO_ACTIVIDAD,
    ID_ESTADO_ACTIVIDAD,
    ID_CUADRILLA,
    TITULO,
    DESCRIPCION,
    FECHA_ACTIVIDAD,
    HORA_INICIO,
    HORA_FIN,
    FECHA_PROGRAMADA,
    FECHA_REALIZACION,
    LUGAR_ESPECIFICO,
    OBSERVACIONES
)
SELECT
    t.ID_TIPO_ACTIVIDAD,
    s.ID_ESTADO_ACTIVIDAD,
    c.ID_CUADRILLA,
    d.TITULO,
    d.DESCRIPCION,
    DATE_ADD(CURRENT_DATE(), INTERVAL d.DIAS DAY),
    d.HORA_INICIO,
    d.HORA_FIN,
    TIMESTAMP(
        DATE_ADD(CURRENT_DATE(), INTERVAL d.DIAS DAY),
        d.HORA_INICIO
    ),
    CASE
        WHEN d.ESTADO = 'COMPLETADA'
        THEN TIMESTAMP(
            DATE_ADD(CURRENT_DATE(), INTERVAL d.DIAS DAY),
            d.HORA_FIN
        )
        ELSE NULL
    END,
    d.LUGAR,
    'Registro de demostracion - Issue #6'
FROM tmp_seed_actividades d
INNER JOIN (
    SELECT MIN(ID_TIPO_ACTIVIDAD) AS ID_TIPO_ACTIVIDAD, NOMBRE
    FROM tipo_actividad
    GROUP BY NOMBRE
) AS t ON t.NOMBRE = d.TIPO
INNER JOIN estados_actividad s
    ON s.NOMBRE_ESTADO = d.ESTADO
INNER JOIN (
    SELECT MIN(ID_CUADRILLA) AS ID_CUADRILLA, NOMBRE
    FROM cuadrillas
    GROUP BY NOMBRE
) AS c ON c.NOMBRE = d.CUADRILLA
WHERE NOT EXISTS (
    SELECT 1
    FROM actividades a
    WHERE a.TITULO = d.TITULO
);

-- Relacionar actividades con colonias SEPOMEX

INSERT INTO actividad_colonia (ID_ACTIVIDAD, ID_COLONIA)
SELECT
    a.ID_ACTIVIDAD,
    co.ID_COLONIA
FROM tmp_seed_actividades d
INNER JOIN (
    SELECT MIN(ID_ACTIVIDAD) AS ID_ACTIVIDAD, TITULO
    FROM actividades
    GROUP BY TITULO
) AS a ON a.TITULO = d.TITULO
INNER JOIN (
    SELECT MIN(ID_COLONIA) AS ID_COLONIA,
           NOMBRE, CODIGO_POSTAL
    FROM colonias
    WHERE MUNICIPIO = 'Saltillo'
    GROUP BY NOMBRE, CODIGO_POSTAL
) AS co
    ON co.NOMBRE = d.COLONIA
   AND co.CODIGO_POSTAL = d.CODIGO_POSTAL
WHERE NOT EXISTS (
    SELECT 1
    FROM actividad_colonia ac
    WHERE ac.ID_ACTIVIDAD = a.ID_ACTIVIDAD
      AND ac.ID_COLONIA = co.ID_COLONIA
);

COMMIT;

DROP TEMPORARY TABLE IF EXISTS tmp_seed_actividades;

-- Verificacion de empleados

SELECT COUNT(*) AS EMPLEADOS_DE_PRUEBA
FROM empleados
WHERE TELEFONO IN (
    '8441000001', '8441000002', '8441000003',
    '8441000004', '8441000005', '8441000006',
    '8441000007', '8441000008', '8441000009'
);

-- Verificacion de cuadrillas

SELECT COUNT(*) AS CUADRILLAS_DE_PRUEBA
FROM cuadrillas
WHERE NOMBRE IN (
    'Cuadrilla Centro',
    'Cuadrilla Norte',
    'Cuadrilla Sur'
);

-- Verificacion de jefes

SELECT COUNT(*) AS JEFES_ASIGNADOS
FROM cuadrilla_empleado ce
INNER JOIN cuadrillas c
    ON c.ID_CUADRILLA = ce.ID_CUADRILLA
WHERE ce.ES_JEFE = 1
  AND c.NOMBRE IN (
      'Cuadrilla Centro',
      'Cuadrilla Norte',
      'Cuadrilla Sur'
  );

-- Verificacion de tipos de actividad

SELECT COUNT(*) AS TIPOS_DE_ACTIVIDAD
FROM tipo_actividad
WHERE NOMBRE IN (
    'Barrido',
    'Poda',
    'Limpieza de baldios',
    'Retiro de escombro'
);

-- Verificacion de actividades

SELECT COUNT(*) AS ACTIVIDADES_DE_PRUEBA
FROM actividades
WHERE TITULO LIKE '[DEMO #6]%';

-- Verificacion de actividades asociadas a colonias

SELECT COUNT(DISTINCT ac.ID_ACTIVIDAD) AS ACTIVIDADES_CON_COLONIA
FROM actividad_colonia ac
INNER JOIN actividades a
    ON a.ID_ACTIVIDAD = ac.ID_ACTIVIDAD
WHERE a.TITULO LIKE '[DEMO #6]%';
