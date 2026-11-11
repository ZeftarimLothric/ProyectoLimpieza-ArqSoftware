package com.saltillo.limpieza.repositories;

/**
 * Se lanza cuando una operación viola una regla del negocio (por ejemplo, "un empleado no puede
 * estar en dos cuadrillas activas"). Su mensaje está pensado para mostrarse tal cual al usuario.
 */
public class ReglaNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
