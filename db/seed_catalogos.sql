-- Datos de PRUEBA para probar los CRUD de catálogos (Issues #8 y #9).
-- No son datos oficiales (la colonias/CP son de ejemplo, no SEPOMEX completo).
-- Es seguro correrlo más de una vez: solo inserta si la tabla está vacía.
USE gestion_limpieza;

INSERT INTO empleados (NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, TELEFONO, PUESTO, ACTIVO)
SELECT t.n, t.ap, t.am, t.tel, t.p, 1 FROM (
    SELECT 'Elena' n, 'Fuentes' ap, 'Ruiz' am, '8441234501' tel, 'Arquitecta supervisora' p
    UNION ALL SELECT 'Carlos', 'Mendoza', 'Lara', '8441234502', 'Ingeniero de zona'
    UNION ALL SELECT 'Pedro', 'Morales', NULL, '8441234503', 'Técnico'
    UNION ALL SELECT 'Roberto', 'Dávila', 'Soto', '8441234504', 'Licenciado supervisor'
    UNION ALL SELECT 'Claudia', 'Salazar', 'Vega', '8441234505', 'Ingeniera de zona'
    UNION ALL SELECT 'Gabriel', 'Treviño', 'Cantú', NULL, 'Técnico'
    UNION ALL SELECT 'Luis', 'Ramírez', 'Ibarra', '8441234507', 'Operador'
    UNION ALL SELECT 'María', 'Hernández', 'Ponce', '8441234508', 'Operadora'
    UNION ALL SELECT 'José', 'García', 'Montes', '8441234509', 'Chofer'
    UNION ALL SELECT 'Ana', 'López', 'Cruz', '8441234510', 'Barrendera'
    UNION ALL SELECT 'Jorge', 'Martínez', 'Reyes', '8441234511', 'Barrendero'
    UNION ALL SELECT 'Sofía', 'Torres', 'Aguirre', '8441234512', 'Operadora'
) t WHERE NOT EXISTS (SELECT 1 FROM empleados);

INSERT INTO tipo_actividad (NOMBRE, DESCRIPCION)
SELECT t.n, t.d FROM (
    SELECT 'Barrido manual' n, 'Barrido de calles y banquetas con personal a pie' d
    UNION ALL SELECT 'Barrido mecánico', 'Barrido con maquinaria en vialidades principales'
    UNION ALL SELECT 'Recolección de residuos', 'Recolección de basura y residuos voluminosos'
    UNION ALL SELECT 'Limpieza de áreas verdes', 'Limpieza y retiro de maleza en parques y camellones'
) t WHERE NOT EXISTS (SELECT 1 FROM tipo_actividad);

INSERT INTO colonias (NOMBRE, CODIGO_POSTAL, MUNICIPIO, ESTADO)
SELECT t.n, t.cp, 'Saltillo', 'Coahuila de Zaragoza' FROM (
    SELECT 'Zona Centro' n, '25000' cp
    UNION ALL SELECT 'República Oriente', '25280'
    UNION ALL SELECT 'República Poniente', '25200'
    UNION ALL SELECT 'Mirasierra', '25016'
    UNION ALL SELECT 'Saltillo 2000', '25014'
    UNION ALL SELECT 'Bellavista', '25060'
) t WHERE NOT EXISTS (SELECT 1 FROM colonias);
