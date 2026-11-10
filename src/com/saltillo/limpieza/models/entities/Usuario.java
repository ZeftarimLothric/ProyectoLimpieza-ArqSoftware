
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;

/**
 * Entidad de dominio que representa a un usuario del sistema.
 *
 * Utilizada por:
 * - Issue #2: Entidades del modelo.
 * - Issue #7: Login y autenticacion.
 * - Issue #4: Sesion de usuario.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombre;
    private String usuario;
    private String contrasenaHash;
    private String rol;
    private boolean activo;

    // Constructor vacio
    public Usuario() {
    }

    // Constructor completo
    public Usuario(int idUsuario, String nombre, String usuario,
                   String contrasenaHash, String rol, boolean activo) {

        setIdUsuario(idUsuario);
        setNombre(nombre);
        setUsuario(usuario);
        setContrasenaHash(contrasenaHash);
        setRol(rol);
        setActivo(activo);
    }

    // Constructor para sesiones y login
    public Usuario(int idUsuario, String nombre, String usuario, String rol) {
        this(idUsuario, nombre, usuario, null, rol, true);
    }

    // ID Usuario
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        if (idUsuario < 0) {
            throw new IllegalArgumentException(
                "El ID del usuario no puede ser negativo."
            );
        }
        this.idUsuario = idUsuario;
    }

    // Nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre del usuario no puede estar vacio."
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

    // Nombre de usuario
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre de usuario no puede estar vacio."
            );
        }

        String valor = usuario.trim();

        if (valor.length() > 50) {
            throw new IllegalArgumentException(
                "El nombre de usuario no puede superar los 50 caracteres."
            );
        }

        this.usuario = valor;
    }

    // Contrasena hash
    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {

        // Se permite null para objetos utilizados en sesiones.
        if (contrasenaHash == null) {
            this.contrasenaHash = null;
            return;
        }

        if (!contrasenaHash.matches("[a-fA-F0-9]{64}")) {
            throw new IllegalArgumentException(
                "La contrasena hash debe contener 64 caracteres"
            );
        }

        this.contrasenaHash = contrasenaHash;
    }

    // Rol
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El rol del usuario no puede estar vacio."
            );
        }

        this.rol = rol.trim();
    }

    // Estado activo
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
