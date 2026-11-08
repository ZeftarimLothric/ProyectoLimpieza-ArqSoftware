package com.saltillo.limpieza.models.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO que encapsula los criterios de búsqueda y filtrado de actividades de limpieza.
 * 
 * Contrato para:
 * - Integrante 4 (Luis Eduardo - Issue #16: Consulta de actividades con filtros)
 * - Integrante 1 (Ximena - Issue #17: SqlDashboardRepository - consultas WHERE dinámicas)
 */
public class FiltroActividadDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idCuadrilla;
    private String colonia;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public FiltroActividadDTO() {
    }

    public FiltroActividadDTO(Integer idCuadrilla, String colonia, String estado, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idCuadrilla = idCuadrilla;
        this.colonia = colonia != null && !colonia.isBlank() ? colonia.trim() : null;
        this.estado = estado != null && !estado.isBlank() ? estado.trim().toUpperCase() : null;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Integer getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(Integer idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia != null && !colonia.isBlank() ? colonia.trim() : null;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado != null && !estado.isBlank() ? estado.trim().toUpperCase() : null;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean tieneFiltroCuadrilla() {
        return idCuadrilla != null && idCuadrilla > 0;
    }

    public boolean tieneFiltroColonia() {
        return colonia != null && !colonia.isBlank();
    }

    public boolean tieneFiltroEstado() {
        return estado != null && !estado.isBlank();
    }

    public boolean tieneFiltroFechaInicio() {
        return fechaInicio != null;
    }

    public boolean tieneFiltroFechaFin() {
        return fechaFin != null;
    }
}
