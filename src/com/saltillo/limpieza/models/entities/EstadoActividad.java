
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/**
 * Entidad de dominio que representa el estado
 * de una actividad de limpieza.
 *
 * Corresponde a la tabla estados_actividad de MySQL.
 *
 * Issue #2 - Entidades del modelo.
 */
public class EstadoActividad implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idEstadoActividad;
    private String nombreEstado;

    // Constructor vacio
    public EstadoActividad() {
    }

    // Constructor completo
    public EstadoActividad(int idEstadoActividad, String nombreEstado) {
        setIdEstadoActividad(idEstadoActividad);
        setNombreEstado(nombreEstado);
    }

    // ID Estado Actividad
    public int getIdEstadoActividad() {
        return idEstadoActividad;
    }

    public void setIdEstadoActividad(int idEstadoActividad) {
        if (idEstadoActividad < 0) {
            throw new IllegalArgumentException(
                "El ID del estado de actividad no puede ser negativo."
            );
        }

        this.idEstadoActividad = idEstadoActividad;
    }

    // Nombre del estado
    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        if (nombreEstado == null || nombreEstado.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre del estado no puede estar vacio."
            );
        }

        String valor = nombreEstado.trim();

        if (valor.length() > 50) {
            throw new IllegalArgumentException(
                "El nombre del estado no puede superar los 50 caracteres."
            );
        }

        this.nombreEstado = valor;
    }

    @Override
    public String toString() {
        return nombreEstado;
    }
}
