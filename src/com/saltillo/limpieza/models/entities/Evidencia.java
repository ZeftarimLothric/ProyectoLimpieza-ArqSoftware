
package com.saltillo.limpieza.models.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una evidencia
 * fotografica asociada a una actividad de limpieza.
 *
 * Corresponde a la tabla evidencia de MySQL.
 *
 * Issue #2 - Entidades del modelo.
 */
public class Evidencia implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idEvidencia;
    private int idActividad;
    private String rutaImagen;
    private String descripcion;
    private LocalDateTime fechaCarga;

    // Constructor vacio
    public Evidencia() {
    }

    // Constructor completo
    public Evidencia(int idEvidencia, int idActividad,
                     String rutaImagen, String descripcion,
                     LocalDateTime fechaCarga) {

        setIdEvidencia(idEvidencia);
        setIdActividad(idActividad);
        setRutaImagen(rutaImagen);
        setDescripcion(descripcion);
        setFechaCarga(fechaCarga);
    }

    // ID Evidencia
    public int getIdEvidencia() {
        return idEvidencia;
    }

    public void setIdEvidencia(int idEvidencia) {
        if (idEvidencia < 0) {
            throw new IllegalArgumentException(
                "El ID de la evidencia no puede ser negativo."
            );
        }

        this.idEvidencia = idEvidencia;
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

    // Ruta de imagen
    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        if (rutaImagen == null || rutaImagen.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "La ruta de la imagen no puede estar vacia."
            );
        }

        String valor = rutaImagen.trim();

        if (valor.length() > 255) {
            throw new IllegalArgumentException(
                "La ruta de la imagen no puede superar los 255 caracteres."
            );
        }

        this.rutaImagen = valor;
    }

    // Descripcion (opcional)
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            this.descripcion = null;
            return;
        }

        String valor = descripcion.trim();

        if (valor.length() > 200) {
            throw new IllegalArgumentException(
                "La descripcion no puede superar los 200 caracteres."
            );
        }

        this.descripcion = valor;
    }

    // Fecha de carga
    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        // Null indica que MySQL asignara CURRENT_TIMESTAMP
        // cuando se inserte el registro.
        this.fechaCarga = fechaCarga;
    }

    @Override
    public String toString() {
        return "Evidencia #" + idEvidencia + " - " + rutaImagen;
    }
}
