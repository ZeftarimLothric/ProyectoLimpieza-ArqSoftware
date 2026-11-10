package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/** Entidad de dominio Cuadrilla (tabla de la BD). Mínima para los repositorios del Issue #3; si el Issue #2 la define, se conserva la del #2. */
public class Cuadrilla implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idCuadrilla;
    private String nombre;
    private boolean activo;

    public Cuadrilla() {
    }

    public Cuadrilla(int idCuadrilla, String nombre, boolean activo) {
        this.idCuadrilla = idCuadrilla;
        this.nombre = nombre;
        this.activo = activo;
    }

    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(int idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
