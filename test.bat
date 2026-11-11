@echo off
echo ========================================================
echo  Ejecutando Pruebas Arquitectonicas y Verificacion MVC
echo ========================================================

java -ea -cp bin com.saltillo.limpieza.test.ArchitectureVerificationTest
java -ea -cp bin com.saltillo.limpieza.test.EvidenciaStorageTest
java -ea -cp bin com.saltillo.limpieza.test.ActividadCrudTest
java -ea -cp bin com.saltillo.limpieza.test.ColoniaSelectorTest

rem La prueba de integracion MySQL necesita el driver (mismo classpath que run.bat); sin BD se omite sola.
set "CP=bin"
if exist "lib\*" set "CP=%CP%;lib\*"
if exist "%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar" (
    set "CP=%CP%;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar"
)
java -ea -cp "%CP%" com.saltillo.limpieza.test.SqlActividadIntegracionTest
pause
