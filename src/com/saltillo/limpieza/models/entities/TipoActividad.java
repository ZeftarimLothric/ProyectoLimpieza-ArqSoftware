package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/** Entidad de dominio TipoActividad (tabla de la BD). Mínima para los repositorios del Issue #3; si el Issue #2 la define, se conserva la del #2. */
public class TipoActividad implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idTipoActividad;
    private String nombre;
    private String descripcion;

    public TipoActividad() {
    }

    public TipoActividad(int idTipoActividad, String nombre, String descripcion) {
        this.idTipoActividad = idTipoActividad;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdTipoActividad() {
        return idTipoActividad;
    }

    public void setIdTipoActividad(int idTipoActividad) {
        this.idTipoActividad = idTipoActividad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
