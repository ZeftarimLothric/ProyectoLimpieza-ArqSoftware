
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa una actividad de limpieza.
 *
 * Corresponde a la tabla actividades de MySQL.
 * Incluye relaciones con cuadrillas, tipos de actividad,
 * estados, colonias y evidencias.
 *
 * Issue #2 - Entidades del modelo.
 */
public class Actividad implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idActividad;
    private TipoActividad tipoActividad;
    private EstadoActividad estadoActividad;
    private Cuadrilla cuadrilla;

    private String titulo;
    private String descripcion;
    private LocalDate fechaActividad;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String lugarEspecifico;
    private String observaciones;

    private List<Colonia> colonias;
    private List<Evidencia> evidencias;

    // Constructor vacio
    public Actividad() {
        this.colonias = new ArrayList<>();
        this.evidencias = new ArrayList<>();
    }

    // Constructor completo
    public Actividad(int idActividad, TipoActividad tipoActividad,
                     EstadoActividad estadoActividad, Cuadrilla cuadrilla,
                     String titulo, String descripcion,
                     LocalDate fechaActividad, LocalTime horaInicio,
                     LocalTime horaFin, String lugarEspecifico,
                     String observaciones, List<Colonia> colonias,
                     List<Evidencia> evidencias) {

        this();

        setIdActividad(idActividad);
        setTipoActividad(tipoActividad);
        setEstadoActividad(estadoActividad);
        setCuadrilla(cuadrilla);
        setTitulo(titulo);
        setDescripcion(descripcion);
        setFechaActividad(fechaActividad);
        setHoraInicio(horaInicio);
        setHoraFin(horaFin);
        setLugarEspecifico(lugarEspecifico);
        setObservaciones(observaciones);
        setColonias(colonias);
        setEvidencias(evidencias);
    }

    // ID Actividad
    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        if (idActividad < 0) {
            throw new IllegalArgumentException(
                "El ID de la actividad no puede ser negativo."
            );
        }
        this.idActividad = idActividad;
    }

    // Tipo de actividad
    public TipoActividad getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(TipoActividad tipoActividad) {
        if (tipoActividad == null) {
            throw new IllegalArgumentException(
                "El tipo de actividad es obligatorio."
            );
        }
        this.tipoActividad = tipoActividad;
    }

    // Estado de actividad
    public EstadoActividad getEstadoActividad() {
        return estadoActividad;
    }

    public void setEstadoActividad(EstadoActividad estadoActividad) {
        if (estadoActividad == null) {
            throw new IllegalArgumentException(
                "El estado de la actividad es obligatorio."
            );
        }
        this.estadoActividad = estadoActividad;
    }

    // Cuadrilla
    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public void setCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new IllegalArgumentException(
                "La cuadrilla es obligatoria."
            );
        }
        this.cuadrilla = cuadrilla;
    }

    // Titulo
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El titulo no puede estar vacio."
            );
        }

        String valor = titulo.trim();

        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                "El titulo no puede superar los 100 caracteres."
            );
        }

        this.titulo = valor;
    }

    // Descripcion
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "La descripcion no puede estar vacia."
            );
        }

        String valor = descripcion.trim();

        if (valor.length() > 200) {
            throw new IllegalArgumentException(
                "La descripcion no puede superar los 200 caracteres."
            );
        }

        this.descripcion = valor;
    }

    // Fecha de actividad
    public LocalDate getFechaActividad() {
        return fechaActividad;
    }

    public void setFechaActividad(LocalDate fechaActividad) {
        if (fechaActividad == null) {
            throw new IllegalArgumentException(
                "La fecha de actividad es obligatoria."
            );
        }

        this.fechaActividad = fechaActividad;
    }

    // Hora de inicio
    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        if (horaInicio != null && horaFin != null &&
            !horaInicio.isBefore(horaFin)) {

            throw new IllegalArgumentException(
                "La hora de inicio debe ser anterior a la hora de fin."
            );
        }

        this.horaInicio = horaInicio;
    }

    // Hora de fin
    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        if (horaFin != null && horaInicio != null &&
            !horaFin.isAfter(horaInicio)) {

            throw new IllegalArgumentException(
                "La hora de fin debe ser posterior a la hora de inicio."
            );
        }

        this.horaFin = horaFin;
    }

    // Lugar especifico
    public String getLugarEspecifico() {
        return lugarEspecifico;
    }

    public void setLugarEspecifico(String lugarEspecifico) {
        if (lugarEspecifico == null ||
            lugarEspecifico.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "El lugar especifico no puede estar vacio."
            );
        }

        String valor = lugarEspecifico.trim();

        if (valor.length() > 200) {
            throw new IllegalArgumentException(
                "El lugar especifico no puede superar los 200 caracteres."
            );
        }

        this.lugarEspecifico = valor;
    }

    // Observaciones (opcionales)
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            this.observaciones = null;
        } else {
            this.observaciones = observaciones.trim();
        }
    }

    // Colonias
    public List<Colonia> getColonias() {
        return new ArrayList<>(colonias);
    }

    public void setColonias(List<Colonia> colonias) {
        List<Colonia> nuevasColonias = new ArrayList<>();

        if (colonias != null) {
            for (Colonia colonia : colonias) {
                if (colonia == null) {
                    throw new IllegalArgumentException(
                        "La lista de colonias no puede contener valores nulos."
                    );
                }

                for (Colonia existente : nuevasColonias) {
                    if (existente.getIdColonia() == colonia.getIdColonia()) {
                        throw new IllegalArgumentException(
                            "No se pueden agregar colonias duplicadas."
                        );
                    }
                }

                nuevasColonias.add(colonia);
            }
        }

        this.colonias = nuevasColonias;
    }

    public void agregarColonia(Colonia colonia) {
        if (colonia == null) {
            throw new IllegalArgumentException(
                "La colonia no puede ser nula."
            );
        }

        for (Colonia existente : colonias) {
            if (existente.getIdColonia() == colonia.getIdColonia()) {
                throw new IllegalArgumentException(
                    "La colonia ya esta asignada a la actividad."
                );
            }
        }

        colonias.add(colonia);
    }

    // Evidencias
    public List<Evidencia> getEvidencias() {
        return new ArrayList<>(evidencias);
    }

    public void setEvidencias(List<Evidencia> evidencias) {
        List<Evidencia> nuevasEvidencias = new ArrayList<>();

        if (evidencias != null) {
            for (Evidencia evidencia : evidencias) {
                if (evidencia == null) {
                    throw new IllegalArgumentException(
                        "La lista de evidencias no puede contener valores nulos."
                    );
                }
                nuevasEvidencias.add(evidencia);
            }
        }

        this.evidencias = nuevasEvidencias;
    }

    public void agregarEvidencia(Evidencia evidencia) {
        if (evidencia == null) {
            throw new IllegalArgumentException(
                "La evidencia no puede ser nula."
            );
        }

        evidencias.add(evidencia);
    }

    @Override
    public String toString() {
        return "Actividad #" + idActividad + " - " + titulo;
    }
}
