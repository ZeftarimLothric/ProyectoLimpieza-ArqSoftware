package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/** Entidad de dominio Colonia (tabla de la BD). Mínima para los repositorios del Issue #3; si el Issue #2 la define, se conserva la del #2. */
public class Colonia implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idColonia;
    private String nombre;
    private String codigoPostal;
    private String municipio;
    private String estado;

    public Colonia() {
    }

    public Colonia(int idColonia, String nombre, String codigoPostal, String municipio, String estado) {
        this.idColonia = idColonia;
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
        this.municipio = municipio;
        this.estado = estado;
    }

    public int getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(int idColonia) {
        this.idColonia = idColonia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigoPostal + ")";
    }
}
