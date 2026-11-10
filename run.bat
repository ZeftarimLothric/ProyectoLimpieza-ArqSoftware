@echo off
echo ========================================================
echo  Iniciando Sistema de Gestion de Limpieza - Saltillo
echo ========================================================

if not exist bin\com\saltillo\limpieza\Main.class (
    echo [INFO] Binarios no encontrados. Compilando primero...
    call build.bat
)

set "CP=bin"
if exist "lib\*" set "CP=%CP%;lib\*"
if exist "%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar" (
    set "CP=%CP%;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar"
)

java -cp "%CP%" com.saltillo.limpieza.Main
