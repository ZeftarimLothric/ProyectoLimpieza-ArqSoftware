package com.saltillo.limpieza.models.dto;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) inmutable que encapsula el resumen de desempeño
 * de una cuadrilla de limpieza municipal.
 * 
 * Cumple con el principio de desacoplamiento del modelo de datos relacional
 * del Integrante 1 y los DAOs del Integrante 2.
 */
public class CuadrillaResumenDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idCuadrilla;
    private final String nombreCuadrilla;
    private final String nombreJefe;
    private final String zonaAsignada;
    private final int totalActividades;
    private final int metaActividades;
    private final double porcentajeCumplimiento;
    private final String estado; // "EN OPERACION", "DISPONIBLE", "EN ESPERA"

    public CuadrillaResumenDTO(int idCuadrilla, 
                               String nombreCuadrilla, 
                               String nombreJefe, 
                               String zonaAsignada, 
                               int totalActividades, 
                               int metaActividades, 
                               String estado) {
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
        this.nombreJefe = nombreJefe;
        this.zonaAsignada = zonaAsignada;
        this.totalActividades = totalActividades;
        this.metaActividades = metaActividades <= 0 ? 1 : metaActividades;
        
        double calc = ((double) totalActividades / (double) this.metaActividades) * 100.0;
        this.porcentajeCumplimiento = Math.min(100.0, Math.round(calc * 10.0) / 10.0);
        this.estado = estado;
    }

    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }

    public String getNombreJefe() {
        return nombreJefe;
    }

    public String getZonaAsignada() {
        return zonaAsignada;
    }

    public int getTotalActividades() {
        return totalActividades;
    }

    public int getMetaActividades() {
        return metaActividades;
    }

    public double getPorcentajeCumplimiento() {
        return porcentajeCumplimiento;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "CuadrillaResumenDTO{" +
                "id=" + idCuadrilla +
                ", nombre='" + nombreCuadrilla + '\'' +
                ", jefe='" + nombreJefe + '\'' +
                ", actividades=" + totalActividades +
                ", cumplimiento=" + porcentajeCumplimiento + "%" +
                '}';
    }
}
