
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/**
 * Entidad de dominio que representa una colonia
 * del Sistema de Gestion de Actividades de Limpieza.
 *
 * Corresponde a la tabla colonias de MySQL.
 *
 * Issue #2 - Entidades del modelo.
 */
public class Colonia implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idColonia;
    private String nombre;
    private String codigoPostal;
    private String municipio;
    private String estado;

    // Constructor vacio
    public Colonia() {
    }

    // Constructor completo
   public Colonia(int idColonia, String nombre, String codigoPostal,
               String municipio, String estado) {
    setIdColonia(idColonia);
    setNombre(nombre);
    setCodigoPostal(codigoPostal);
    setMunicipio(municipio);
    setEstado(estado);
}
    // ID Colonia
    public int getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(int idColonia) {
        if (idColonia < 0) {
            throw new IllegalArgumentException(
                "El ID de la colonia no puede ser negativo."
            );
        }

        this.idColonia = idColonia;
    }

    // Nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre de la colonia no puede estar vacio."
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

    // Codigo postal
    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        if (codigoPostal == null ||
            !codigoPostal.matches("\\d{5}")) {

            throw new IllegalArgumentException(
                "El codigo postal debe contener exactamente 5 digitos."
            );
        }

        this.codigoPostal = codigoPostal;
    }

    // Municipio
    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        if (municipio == null || municipio.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El municipio no puede estar vacio."
            );
        }

        String valor = municipio.trim();

        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                "El municipio no puede superar los 100 caracteres."
            );
        }

        this.municipio = valor;
    }

    // Estado
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El estado no puede estar vacio."
            );
        }

        String valor = estado.trim();

        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                "El estado no puede superar los 100 caracteres."
            );
        }

        this.estado = valor;
    }

    @Override
    public String toString() {
        return nombre + " - C.P. " + codigoPostal
                + " (" + municipio + ", " + estado + ")";
    }
}
