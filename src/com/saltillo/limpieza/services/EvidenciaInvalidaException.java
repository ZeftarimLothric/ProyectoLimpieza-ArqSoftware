package com.saltillo.limpieza.services;

/**
 * Excepción de Evidencias (Integrante 3 - Issue #12).
 *
 * Su mensaje siempre está redactado para mostrarse directamente al usuario.
 */
public class EvidenciaInvalidaException extends Exception {
    private static final long serialVersionUID = 1L;

    public EvidenciaInvalidaException(String mensaje) {
        super(mensaje);
    }

    public EvidenciaInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
