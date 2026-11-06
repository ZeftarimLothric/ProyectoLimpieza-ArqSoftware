@echo off
echo ========================================================
echo  Iniciando Sistema de Gestion de Limpieza - Saltillo
echo ========================================================

if not exist bin\com\saltillo\limpieza\Main.class (
    echo [INFO] Binarios no encontrados. Compilando primero...
    call build.bat
)

java -cp bin com.saltillo.limpieza.Main
