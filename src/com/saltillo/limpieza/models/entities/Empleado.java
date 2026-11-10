
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/**
 * Entidad de dominio que representa a un empleado
 * del Sistema de Gestion de Actividades de Limpieza.
 *
 * Corresponde a la tabla empleados de la base de datos.
 *
 * Issue #2 - Entidades del modelo.
 */
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idEmpleado;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private String puesto;
    private boolean activo;

    // Constructor vacio
    public Empleado() {
    }

    // Constructor completo
    public Empleado(int idEmpleado, String nombre,
                    String apellidoPaterno, String apellidoMaterno,
                    String telefono, String puesto, boolean activo) {

        setIdEmpleado(idEmpleado);
        setNombre(nombre);
        setApellidoPaterno(apellidoPaterno);
        setApellidoMaterno(apellidoMaterno);
        setTelefono(telefono);
        setPuesto(puesto);
        setActivo(activo);
    }

    // ID Empleado
    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        if (idEmpleado < 0) {
            throw new IllegalArgumentException(
                "El ID del empleado no puede ser negativo."
            );
        }

        this.idEmpleado = idEmpleado;
    }

    // Nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre del empleado no puede estar vacio."
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

    // Apellido paterno
    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        if (apellidoPaterno == null || apellidoPaterno.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El apellido paterno no puede estar vacio."
            );
        }

        String valor = apellidoPaterno.trim();

        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                "El apellido paterno no puede superar los 100 caracteres."
            );
        }

        this.apellidoPaterno = valor;
    }

    // Apellido materno (opcional)
    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        if (apellidoMaterno == null || apellidoMaterno.trim().isEmpty()) {
            this.apellidoMaterno = null;
            return;
        }

        String valor = apellidoMaterno.trim();

        if (valor.length() > 100) {
            throw new IllegalArgumentException(
                "El apellido materno no puede superar los 100 caracteres."
            );
        }

        this.apellidoMaterno = valor;
    }

    // Telefono (opcional)
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            this.telefono = null;
            return;
        }

        String valor = telefono.trim();

        if (valor.length() > 15) {
            throw new IllegalArgumentException(
                "El telefono no puede superar los 15 caracteres."
            );
        }

        this.telefono = valor;
    }

    // Puesto
    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        if (puesto == null || puesto.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El puesto del empleado no puede estar vacio."
            );
        }

        String valor = puesto.trim();

        if (valor.length() > 50) {
            throw new IllegalArgumentException(
                "El puesto no puede superar los 50 caracteres."
            );
        }

        this.puesto = valor;
    }

    // Estado activo
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Nombre completo
    public String getNombreCompleto() {
        String nombreCompleto = nombre + " " + apellidoPaterno;

        if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
            nombreCompleto += " " + apellidoMaterno;
        }

        return nombreCompleto;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " - " + puesto;
    }
}
