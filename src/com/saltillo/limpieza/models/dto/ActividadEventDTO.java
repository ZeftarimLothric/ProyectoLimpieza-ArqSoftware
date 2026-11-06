package com.saltillo.limpieza.models.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Event DTO que transporta los detalles de una nueva actividad de limpieza
 * registrada en el sistema.
 * 
 * Se emite a través del patrón Observer cuando el Integrante 3 registra una
 * actividad o cuando se ejecuta una simulación en tiempo real.
 */
public class ActividadEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idCuadrilla;
    private final String nombreCuadrilla;
    private final String colonia;
    private final String tipoLimpieza;
    private final String observaciones;
    private final LocalDateTime fechaHora;

    public ActividadEventDTO(int idCuadrilla, 
                             String nombreCuadrilla, 
                             String colonia, 
                             String tipoLimpieza, 
                             String observaciones) {
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
        this.colonia = colonia;
        this.tipoLimpieza = tipoLimpieza;
        this.observaciones = observaciones;
        this.fechaHora = LocalDateTime.now();
    }

    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }

    public String getColonia() {
        return colonia;
    }

    public String getTipoLimpieza() {
        return tipoLimpieza;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getFechaHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return fechaHora.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("[%s] Cuadrilla #%d (%s) realizó '%s' en Col. %s",
                getFechaHoraFormateada(), idCuadrilla, nombreCuadrilla, tipoLimpieza, colonia);
    }
}
