
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio que representa una cuadrilla
 * encargada de realizar actividades de limpieza.
 *
 * Corresponde a la tabla cuadrillas.
 * Sus empleados se relacionan mediante cuadrilla_empleado.
 *
 * Issue #2 - Entidades del modelo.
 */
public class Cuadrilla implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idCuadrilla;
    private String nombre;
    private boolean activo;

    private Empleado jefe;
    private List<Empleado> integrantes;

    // Constructor vacio
    public Cuadrilla() {
        this.integrantes = new ArrayList<>();
    }

    // Constructor completo
    public Cuadrilla(int idCuadrilla, String nombre, boolean activo,
                     Empleado jefe, List<Empleado> integrantes) {

        this();
        setIdCuadrilla(idCuadrilla);
        setNombre(nombre);
        setActivo(activo);
        setIntegrantes(integrantes);
        setJefe(jefe);
    }

    // ID Cuadrilla
    public int getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(int idCuadrilla) {
        if (idCuadrilla < 0) {
            throw new IllegalArgumentException(
                "El ID de la cuadrilla no puede ser negativo."
            );
        }

        this.idCuadrilla = idCuadrilla;
    }

    // Nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre de la cuadrilla no puede estar vacio."
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

    // Estado activo
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Jefe de cuadrilla
    public Empleado getJefe() {
        return jefe;
    }

    public void setJefe(Empleado jefe) {
        if (jefe != null) {
            if (jefe.getIdEmpleado() <= 0) {
                throw new IllegalArgumentException(
                    "El jefe debe tener un ID de empleado valido."
                );
            }

            if (!jefe.isActivo()) {
                throw new IllegalArgumentException(
                    "El jefe de cuadrilla debe estar activo."
                );
            }
        }

        this.jefe = jefe;

        if (jefe != null && !contieneEmpleado(jefe.getIdEmpleado())) {
            integrantes.add(jefe);
        }
    }

    // Lista de integrantes
    public List<Empleado> getIntegrantes() {
        return new ArrayList<>(integrantes);
    }

    public void setIntegrantes(List<Empleado> integrantes) {
        this.integrantes = new ArrayList<>();

        if (integrantes != null) {
            for (Empleado empleado : integrantes) {
                agregarIntegrante(empleado);
            }
        }

        if (jefe != null && !contieneEmpleado(jefe.getIdEmpleado())) {
            this.integrantes.add(jefe);
        }
    }

    // Agregar empleado a la cuadrilla
    public void agregarIntegrante(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException(
                "El empleado no puede ser nulo."
            );
        }

        if (empleado.getIdEmpleado() <= 0) {
            throw new IllegalArgumentException(
                "El empleado debe tener un ID valido."
            );
        }

        if (!empleado.isActivo()) {
            throw new IllegalArgumentException(
                "No se puede agregar un empleado inactivo."
            );
        }

        if (contieneEmpleado(empleado.getIdEmpleado())) {
            throw new IllegalArgumentException(
                "El empleado ya pertenece a esta cuadrilla."
            );
        }

        integrantes.add(empleado);
    }

    // Eliminar integrante por ID
    public void eliminarIntegrante(int idEmpleado) {
        if (jefe != null && jefe.getIdEmpleado() == idEmpleado) {
            throw new IllegalArgumentException(
                "No se puede eliminar al jefe de la cuadrilla."
            );
        }

        integrantes.removeIf(
            empleado -> empleado.getIdEmpleado() == idEmpleado
        );
    }

    // Verificar si un empleado pertenece a la cuadrilla
    public boolean contieneEmpleado(int idEmpleado) {
        for (Empleado empleado : integrantes) {
            if (empleado.getIdEmpleado() == idEmpleado) {
                return true;
            }
        }

        return false;
    }

    // Cantidad de integrantes
    public int getCantidadIntegrantes() {
        return integrantes.size();
    }

    @Override
    public String toString() {
        return nombre + " (" + integrantes.size() + " integrantes)";
    }
}
