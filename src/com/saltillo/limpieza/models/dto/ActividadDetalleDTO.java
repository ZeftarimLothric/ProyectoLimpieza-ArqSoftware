package com.saltillo.limpieza.models.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO que representa el detalle completo de una actividad de limpieza
 * retornado por las consultas y filtros de búsqueda.
 * 
 * Contrato para:
 * - Integrante 4 (Luis Eduardo - Issue #16: Consulta de actividades)
 * - Integrante 1 (Ximena - Issue #17: SqlDashboardRepository)
 */
public class ActividadDetalleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idActividad;
    private final int idCuadrilla;
    private final String nombreCuadrilla;
    private final List<String> colonias;
    private final String tipoLimpieza;
    private final String lugarEspecifico;
    private final String estado; // "COMPLETADA", "EN PROCESO", "PROGRAMADA"
    private final LocalDateTime fechaHora;
    private final String observaciones;
    private final LocalDateTime fechaRealizacion; // null si aún no se realiza
    private final String titulo;

    public ActividadDetalleDTO(int idActividad,
                               int idCuadrilla,
                               String nombreCuadrilla,
                               List<String> colonias,
                               String tipoLimpieza,
                               String lugarEspecifico,
                               String estado,
                               LocalDateTime fechaHora,
                               String observaciones) {
        this(idActividad, idCuadrilla, nombreCuadrilla, colonias, tipoLimpieza,
                lugarEspecifico, estado, fechaHora, observaciones, null, null);
    }

    public ActividadDetalleDTO(int idActividad,
                               int idCuadrilla, 
                               String nombreCuadrilla, 
                               List<String> colonias, 
                               String tipoLimpieza, 
                               String lugarEspecifico, 
                               String estado, 
                               LocalDateTime fechaHora, 
                               String observaciones,
                               LocalDateTime fechaRealizacion) {
        this(idActividad, idCuadrilla, nombreCuadrilla, colonias, tipoLimpieza,
                lugarEspecifico, estado, fechaHora, observaciones, fechaRealizacion, null);
    }

    public ActividadDetalleDTO(int idActividad,
                               int idCuadrilla,
                               String nombreCuadrilla,
                               List<String> colonias,
                               String tipoLimpieza,
                               String lugarEspecifico,
                               String estado,
                               LocalDateTime fechaHora,
                               String observaciones,
                               LocalDateTime fechaRealizacion,
                               String titulo) {
        this.idActividad = idActividad;
        this.idCuadrilla = idCuadrilla;
        this.nombreCuadrilla = nombreCuadrilla;
        this.colonias = colonias != null ? new ArrayList<>(colonias) : new ArrayList<>();
        this.tipoLimpieza = tipoLimpieza;
        this.lugarEspecifico = lugarEspecifico;
        this.estado = estado;
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
        this.observaciones = observaciones;
        this.fechaRealizacion = fechaRealizacion;
        this.titulo = (titulo != null && !titulo.trim().isEmpty())
                ? titulo.trim()
                : construirTituloPorDefecto(tipoLimpieza, lugarEspecifico);
    }

    private static String construirTituloPorDefecto(String tipoLimpieza, String lugarEspecifico) {
        String tipo = (tipoLimpieza != null && !tipoLimpieza.isBlank()) ? tipoLimpieza.trim() : "Actividad de limpieza";
        if (lugarEspecifico != null && !lugarEspecifico.isBlank()) {
            String compuesto = tipo + " - " + lugarEspecifico.trim();
            return compuesto.length() > 100 ? compuesto.substring(0, 100) : compuesto;
        }
        return tipo.length() > 100 ? tipo.substring(0, 100) : tipo;
    }

    public int getIdActividad() {
        return idActividad;
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

    public String getColoniasFormateadas() {
        if (colonias.isEmpty()) return "Sin colonia asignada";
        return String.join(", ", colonias);
    }

    public String getTipoLimpieza() {
        return tipoLimpieza;
    }

    public String getLugarEspecifico() {
        return lugarEspecifico;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getFechaHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formatter);
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public String getFechaRealizacionFormateada() {
        if (fechaRealizacion == null) return "";
        return fechaRealizacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public String toString() {
        return String.format("#%d - %s [%s] en %s (%s)",
                idActividad, tipoLimpieza, nombreCuadrilla, getColoniasFormateadas(), estado);
    }
}
