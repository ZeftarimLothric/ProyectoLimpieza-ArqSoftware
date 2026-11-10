package com.saltillo.limpieza.models.dto;

import java.io.Serializable;

/**
 * DTO de una colonia para el selector de colonias atendidas (Integrante 3 - Issue #11).
 *
 * TODO(#2): construirlo a partir de la entidad Colonia cuando exista.
 */
public class ColoniaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idColonia;
    private final String nombre;
    private final String codigoPostal;

    public ColoniaDTO(int idColonia, String nombre, String codigoPostal) {
        this.idColonia = idColonia;
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
    }

    public int getIdColonia() {
        return idColonia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof ColoniaDTO c && c.idColonia == idColonia;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idColonia);
    }

    @Override
    public String toString() {
        return nombre + "  (CP " + codigoPostal + ")";
    }
}
