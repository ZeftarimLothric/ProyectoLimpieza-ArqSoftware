package com.saltillo.limpieza.test;

import com.saltillo.limpieza.security.PasswordHasher;

/**
 * Prueba del Issue #7: hash SHA-256 y comparación de contraseñas.
 * No necesita base de datos. Ejecutar con Run File.
 */
public class PasswordHasherTest {

    private static int fallos = 0;

    private static void verifica(boolean condicion, String descripcion) {
        System.out.println((condicion ? "[OK]    " : "[FALLA] ") + descripcion);
        if (!condicion) {
            fallos++;
        }
    }

    public static void main(String[] args) {
        // Vectores conocidos de SHA-256
        verifica(PasswordHasher.hash("abc").equals(
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"),
                "SHA-256 de \"abc\" coincide con el vector estándar");
        verifica(PasswordHasher.hash("admin123").equals(
                "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"),
                "SHA-256 de \"admin123\" coincide con el del seed");

        String hash = PasswordHasher.hash("admin123");
        verifica(hash.length() == 64, "El hash mide 64 caracteres (cabe en CHAR(64))");
        verifica(!hash.contains("admin123"), "El hash no contiene la contraseña en texto plano");
        verifica(PasswordHasher.hash("admin123").equals(hash), "La misma contraseña produce siempre el mismo hash");

        // Comparación
        verifica(PasswordHasher.verificar("admin123", hash), "Contraseña correcta -> acepta");
        verifica(!PasswordHasher.verificar("Admin123", hash), "Distingue mayúsculas/minúsculas -> rechaza");
        verifica(!PasswordHasher.verificar("admin1234", hash), "Contraseña distinta -> rechaza");
        verifica(!PasswordHasher.verificar("", hash), "Contraseña vacía -> rechaza");
        verifica(!PasswordHasher.verificar(null, hash), "Contraseña nula -> rechaza");
        verifica(!PasswordHasher.verificar("admin123", null), "Hash nulo -> rechaza");
        verifica(PasswordHasher.verificar("admin123", hash.toUpperCase()), "Acepta el hash guardado en mayúsculas");

        System.out.println(fallos == 0 ? "\nTodas las pruebas pasaron." : "\nPruebas fallidas: " + fallos);
        if (fallos > 0) {
            System.exit(1);
        }
    }
}
