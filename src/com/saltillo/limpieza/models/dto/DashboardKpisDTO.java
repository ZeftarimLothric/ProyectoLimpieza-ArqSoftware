package com.saltillo.limpieza.models.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO que consolida los indicadores clave de rendimiento (KPIs) ejecutivos
 * para el Municipio de Saltillo.
 * 
 * Transporta datos agregados calculados en el repositorio/servicio hacia el
 * controlador y la vista sin exponer entidades ORM o conexiones de base de datos.
 */
public class DashboardKpisDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int totalActividadesEjecutadas;
    private final String cuadrillaMasActiva;
    private final int actividadesCuadrillaMasActiva;
    private final int cuadrillasDisponibles;
    private final int totalCuadrillas;
    private final double promedioActividadesPorCuadrilla;
    private final String ultimaActualizacion;

    public DashboardKpisDTO(int totalActividadesEjecutadas,
                            String cuadrillaMasActiva,
                            int actividadesCuadrillaMasActiva,
                            int cuadrillasDisponibles,
                            int totalCuadrillas,
                            double promedioActividadesPorCuadrilla,
                            LocalDateTime timestamp) {
        this.totalActividadesEjecutadas = totalActividadesEjecutadas;
        this.cuadrillaMasActiva = cuadrillaMasActiva;
        this.actividadesCuadrillaMasActiva = actividadesCuadrillaMasActiva;
        this.cuadrillasDisponibles = cuadrillasDisponibles;
        this.totalCuadrillas = totalCuadrillas;
        this.promedioActividadesPorCuadrilla = Math.round(promedioActividadesPorCuadrilla * 10.0) / 10.0;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.ultimaActualizacion = (timestamp != null) ? timestamp.format(formatter) : "N/A";
    }

    public int getTotalActividadesEjecutadas() {
        return totalActividadesEjecutadas;
    }

    public String getCuadrillaMasActiva() {
        return cuadrillaMasActiva;
    }

    public int getActividadesCuadrillaMasActiva() {
        return actividadesCuadrillaMasActiva;
    }

    public int getCuadrillasDisponibles() {
        return cuadrillasDisponibles;
    }

    public int getTotalCuadrillas() {
        return totalCuadrillas;
    }

    public double getPromedioActividadesPorCuadrilla() {
        return promedioActividadesPorCuadrilla;
    }

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }
}
