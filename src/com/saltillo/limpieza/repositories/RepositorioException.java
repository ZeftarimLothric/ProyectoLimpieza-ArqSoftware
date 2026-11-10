package com.saltillo.limpieza.repositories;

/**
 * Error de acceso a datos. Evita que los controladores y vistas dependan de SQLException
 * (la capa de presentación no debe conocer detalles de JDBC).
 */
public class RepositorioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RepositorioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
