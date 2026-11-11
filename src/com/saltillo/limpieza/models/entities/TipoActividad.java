
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/**
 * Entidad de dominio que representa un tipo de actividad
 * del Sistema de Gestion de Actividades de Limpieza.
 *
 * Corresponde a la tabla tipo_actividad de MySQL.
 *
 * Issue #2 - Entidades del modelo.
 */
public class TipoActividad implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idTipoActividad;
    private String nombre;
    private String descripcion;

    // Constructor vacio
    public TipoActividad() {
    }

    // Constructor completo
    public TipoActividad(int idTipoActividad, String nombre,
                         String descripcion) {

        setIdTipoActividad(idTipoActividad);
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    // ID Tipo Actividad
    public int getIdTipoActividad() {
        return idTipoActividad;
    }

    public void setIdTipoActividad(int idTipoActividad) {
        if (idTipoActividad < 0) {
            throw new IllegalArgumentException(
                "El ID del tipo de actividad no puede ser negativo."
            );
        }

        this.idTipoActividad = idTipoActividad;
    }

    // Nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre del tipo de actividad no puede estar vacio."
            );
        }

        String valor = nombre.trim();

        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                "El nombre no puede superar los 100 caracteres."
            );
        }

        this.nombre = valor;
    }

    // Descripcion (opcional)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            this.descripcion = null;
            return;
        }

        String valor = descripcion.trim();

        if (valor.length() > 200) {
            throw new IllegalArgumentException(
                "La descripcion no puede superar los 200 caracteres."
            );
        }

        this.descripcion = valor;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
