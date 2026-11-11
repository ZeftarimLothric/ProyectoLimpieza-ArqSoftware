package com.saltillo.limpieza.models.entities;

import java.io.Serializable;
import java.time.LocalDate;

/** Pertenencia de un empleado a una cuadrilla (tabla cuadrilla_empleado). Issue #9. */
public class IntegranteCuadrilla implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idCuadrillaEmpleado;
    private int idCuadrilla;
    private Empleado empleado;
    private LocalDate fechaAsignacion;
    private boolean jefe;

    public IntegranteCuadrilla() {
    }

    public IntegranteCuadrilla(int idCuadrillaEmpleado, int idCuadrilla, Empleado empleado,
                               LocalDate fechaAsignacion, boolean jefe) {
        this.idCuadrillaEmpleado = idCuadrillaEmpleado;
        this.idCuadrilla = idCuadrilla;
        this.empleado = empleado;
        this.fechaAsignacion = fechaAsignacion;
        this.jefe = jefe;
    }

    public int getIdCuadrillaEmpleado() {
        return idCuadrillaEmpleado;
    }

    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public boolean isJefe() {
        return jefe;
    }
}
