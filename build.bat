@echo off
echo ========================================================
echo  Compilando Sistema de Gestion de Limpieza - Saltillo
echo  Modulo Integrante 4 (Shell, Dashboard, Observer)
echo ========================================================

if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo [EXITO] Compilacion completada sin errores.
    del sources.txt
) else (
    echo [ERROR] Hubo un error en la compilacion.
    del sources.txt
)
pause
