package com.saltillo.limpieza.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad de hash de contraseñas con SHA-256 (Issue #7).
 *
 * Devuelve 64 caracteres hexadecimales en minúsculas, que es lo que se guarda en
 * usuarios.CONTRASENA (CHAR(64)). La contraseña en texto plano nunca se almacena ni se compara.
 */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    /** SHA-256 en hexadecimal (64 caracteres) del texto recibido. */
    public static String hash(String textoPlano) {
        if (textoPlano == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible en esta JVM.", e);
        }
    }

    /** true si el hash del texto plano coincide con el hash almacenado. */
    public static boolean verificar(String textoPlano, String hashAlmacenado) {
        if (textoPlano == null || hashAlmacenado == null) {
            return false;
        }
        // Comparación en tiempo constante (evita ataques por tiempo de respuesta).
        return MessageDigest.isEqual(
                hash(textoPlano).getBytes(StandardCharsets.UTF_8),
                hashAlmacenado.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}
