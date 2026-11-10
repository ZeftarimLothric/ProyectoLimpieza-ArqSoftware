
-- ISSUE #5 - Catalogo SEPOMEX Saltillo
-- Ejecutar despues de db/schema.sql

-- Agregar el tipo de asentamiento solo si no existe
SET @existe_columna = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'colonias'
      AND COLUMN_NAME = 'TIPO_ASENTAMIENTO'
);

SET @consulta = IF(
    @existe_columna = 0,
    'ALTER TABLE colonias ADD COLUMN TIPO_ASENTAMIENTO VARCHAR(100) NULL',
    'SELECT ''La columna TIPO_ASENTAMIENTO ya existe'''
);

PREPARE sentencia FROM @consulta;
EXECUTE sentencia;
DEALLOCATE PREPARE sentencia;

-- IMPORTACION DEL CATALOGO SEPOMEX
--
-- 1. Descargar el archivo de Coahuila desde Correos de Mexico.
-- 2. Guardarlo como db/CodigosPostales.xls.
-- 3. Ejecutar desde la raiz del proyecto:
--    py db/generar_sepomex.py
-- 4. Se generara db/sepomex_saltillo_local.sql.
-- 5. En MySQL, seleccionar la base de datos gestion_limpieza.
-- 6. Ejecutar el archivo SQL local generado.
--
-- El generador filtra Saltillo y conserva:
-- nombre, codigo postal, municipio, estado y tipo.
--
-- Los archivos descargados y generados son de uso local.

-- Cargar los asentamientos generados localmente
-- Ejecutar desde la raiz del proyecto con MySQL Command Line Client.
SOURCE db/sepomex_saltillo_local.sql;