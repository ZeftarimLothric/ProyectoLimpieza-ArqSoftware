package com.saltillo.limpieza.models.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Event DTO que transporta los detalles de un evento de actividad de limpieza.
 * 
 * Cumplimiento de Contrato Issue #4:
 * - Soporta múltiples colonias (List<String> colonias) conforme al Requisito 8 de la rúbrica.
 * - Transporta el TipoEventoActividad (CREADA, ACTUALIZADA, ELIMINADA) para el Issue #14.
 */
public class ActividadEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idCuadrilla;
    private final String nombreCuadrilla;
    private final List<String> colonias;
    private final String tipoLimpieza;
    private final String observaciones;
    private final TipoEventoActividad tipoEvento;
    private final LocalDateTime fechaHora;

    /**
     * Constructor completo con lista de colonias y tipo de evento.
     */
    public ActividadEventDTO(int idCuadrilla, 
                             String nombreCuadrilla, 
                             List<String> colonias, 
                             String tipoLimpieza, 
                             String observaciones,
                             TipoEventoActividad tipoEvento) {
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
        this.colonias = colonias != null ? new ArrayList<>(colonias) : new ArrayList<>();
        this.tipoLimpieza = tipoLimpieza;
        this.observaciones = observaciones;
        this.tipoEvento = tipoEvento != null ? tipoEvento : TipoEventoActividad.CREADA;
        this.fechaHora = LocalDateTime.now();
    }

    /**
     * Constructor de conveniencia con una sola colonia y tipo de evento explícito.
     */
    public ActividadEventDTO(int idCuadrilla, 
                             String nombreCuadrilla, 
                             String colonia, 
                             String tipoLimpieza, 
                             String observaciones,
                             TipoEventoActividad tipoEvento) {
        this(idCuadrilla, nombreCuadrilla,
                (colonia != null && !colonia.isBlank()) ? List.of(colonia) : List.of(),
                tipoLimpieza, observaciones, tipoEvento);
    }

    /**
     * Constructor de retrocompatibilidad (por defecto tipo CREADA).
     */
    public ActividadEventDTO(int idCuadrilla, 
                             String nombreCuadrilla, 
                             String colonia, 
                             String tipoLimpieza, 
                             String observaciones) {
        this(idCuadrilla, nombreCuadrilla, colonia, tipoLimpieza, observaciones, TipoEventoActividad.CREADA);
    }

    /**
     * Constructor de retrocompatibilidad con lista de colonias y tipo CREADA por defecto.
     */
    public ActividadEventDTO(int idCuadrilla, 
                             String nombreCuadrilla, 
                             List<String> colonias, 
                             String tipoLimpieza, 
                             String observaciones) {
        this(idCuadrilla, nombreCuadrilla, colonias, tipoLimpieza, observaciones, TipoEventoActividad.CREADA);
    }

    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public String getNombreCuadrilla() {
        return nombreCuadrilla;
    }

    public List<String> getColonias() {
        return Collections.unmodifiableList(colonias);
    }

    /**
     * Retorna la primera colonia o 'Sin colonia' (retrocompatibilidad).
     */
    public String getColonia() {
        return colonias.isEmpty() ? "Sin colonia" : colonias.get(0);
    }

    /**
     * Retorna las colonias concatenadas en formato legible.
     */
    public String getColoniasFormateadas() {
        if (colonias.isEmpty()) return "Sin colonia asignada";
        return String.join(", ", colonias);
    }

    public String getTipoLimpieza() {
        return tipoLimpieza;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public TipoEventoActividad getTipoEvento() {
        return tipoEvento;
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
        return String.format("[%s] (%s) Cuadrilla #%d (%s) - %s en %s",
                getFechaHoraFormateada(), tipoEvento, idCuadrilla, nombreCuadrilla, tipoLimpieza, getColoniasFormateadas());
    }
}
