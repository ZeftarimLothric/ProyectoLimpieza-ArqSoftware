@echo off
echo ========================================================
echo  Ejecutando Pruebas Arquitectonicas y Verificacion MVC
echo ========================================================

java -ea -cp bin com.saltillo.limpieza.test.ArchitectureVerificationTest
java -ea -cp bin com.saltillo.limpieza.test.EvidenciaStorageTest
java -ea -cp bin com.saltillo.limpieza.test.ActividadCrudTest
java -ea -cp bin com.saltillo.limpieza.test.ColoniaSelectorTest
pause
