-- Usuario administrador inicial para probar el login (Issue #7).
-- Usuario: admin   Contraseña: admin123   (cámbiala después de la demo)
-- CONTRASENA guarda el SHA-256 de la contraseña, nunca el texto plano.
USE gestion_limpieza;

INSERT INTO usuarios (USERNAME, CONTRASENA, NOMBRE, ACTIVO)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador Municipal', 1)
ON DUPLICATE KEY UPDATE NOMBRE = VALUES(NOMBRE);
