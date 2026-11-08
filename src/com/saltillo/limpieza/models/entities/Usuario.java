package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/**
 * Entidad de dominio que representa a un Usuario del sistema.
 * 
 * Contrato base para los módulos de:
 * - Integrante 1 (Ximena - Issue #2 Entidades del modelo)
 * - Integrante 2 (Eli - Issue #7 Login y Autenticación)
 * - Integrante 4 (Luis Eduardo - Issue #4 SesionUsuario)
 */
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombre;
    private String usuario;
    private String contrasenaHash;
    private String rol; // e.g. "ADMINISTRADOR", "SUPERVISOR"
    private boolean activo;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String usuario, String contrasenaHash, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
        this.activo = activo;
    }

    // Constructor de conveniencia para sesiones o login
    public Usuario(int idUsuario, String nombre, String usuario, String rol) {
        this(idUsuario, nombre, usuario, null, rol, true);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre + " (" + usuario + " - " + rol + ")";
    }
}
