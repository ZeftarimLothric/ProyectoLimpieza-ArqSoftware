package com.saltillo.limpieza.models.dto;

/**
 * Enumeración que define los tipos de eventos emitidos en el ciclo de vida
 * de una actividad de limpieza urbana.
 * 
 * Contrato para:
 * - Integrante 3 (Angel - Issue #14: Publicar eventos al dashboard)
 * - Integrante 4 (Luis Eduardo - Issue #4: Ajustes a contratos)
 */
public enum TipoEventoActividad {
    CREADA("Nueva actividad registrada"),
    ACTUALIZADA("Actividad actualizada"),
    ELIMINADA("Actividad eliminada");

    private final String descripcion;

    TipoEventoActividad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
