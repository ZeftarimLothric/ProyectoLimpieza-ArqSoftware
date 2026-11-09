package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.services.EvidenciaInvalidaException;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.EvidenciaPanel;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Evidencias (Integrante 3 - Issue #12).
 *
 * Une el EvidenciaPanel con el EvidenciaStorageService y lleva la edición en curso:
 * las imágenes originales no se borran hasta confirmar, y las nuevas se borran si se cancela.
 *
 * TODO(#2): persistir los nombres con IEvidenciaRepository cuando exista la entidad Evidencia (se hará en #10).
 */
public class EvidenciaController {

    private final EvidenciaPanel panel;
    private final EvidenciaStorageService servicio;
    private List<String> originales = new ArrayList<>();
    private List<String> evidencias = new ArrayList<>();

    public EvidenciaController(EvidenciaPanel panel, EvidenciaStorageService servicio) {
        this.panel = panel;
        this.servicio = servicio;

        this.panel.setAgregarListener(this::onAgregarSolicitado);
        this.panel.setQuitarListener(this::onQuitarSolicitado);
        this.panel.setAbrirImagenListener(this::onAbrirImagenSolicitada);
    }

    /**
     * Inicia una edición con las evidencias ya guardadas de la actividad.
     */
    public void cargar(List<String> nombres) {
        originales = nombres != null ? new ArrayList<>(nombres) : new ArrayList<>();
        evidencias = new ArrayList<>(originales);
        refrescarPanel();
    }

    public void agregarArchivo(Path archivo) {
        try {
            evidencias.add(servicio.guardar(archivo));
            refrescarPanel();
        } catch (EvidenciaInvalidaException ex) {
            panel.mostrarError(ex.getMessage());
        }
    }

    public void quitar(String nombre) {
        if (!evidencias.remove(nombre)) {
            return;
        }
        if (!originales.contains(nombre)) {
            // Una imagen agregada en esta edición nunca llegó a la BD: se borra de inmediato.
            mostrarErrores(eliminarArchivos(List.of(nombre)));
        }
        refrescarPanel();
    }

    public List<String> getEvidencias() {
        return Collections.unmodifiableList(new ArrayList<>(evidencias));
    }

    /**
     * Se llama al guardar la actividad: borra los archivos que el usuario quitó.
     */
    public void confirmarCambios() {
        List<String> quitadas = new ArrayList<>(originales);
        quitadas.removeAll(evidencias);
        originales = new ArrayList<>(evidencias);
        mostrarErrores(eliminarArchivos(quitadas));
    }

    /**
     * Se llama al cancelar: borra las imágenes nuevas y restaura las originales.
     */
    public void descartarCambios() {
        List<String> nuevas = new ArrayList<>(evidencias);
        nuevas.removeAll(originales);
        evidencias = new ArrayList<>(originales);
        List<String> errores = eliminarArchivos(nuevas);
        refrescarPanel();
        mostrarErrores(errores);
    }

    private void onAgregarSolicitado(ActionEvent e) {
        List<String> errores = new ArrayList<>();
        for (Path archivo : panel.seleccionarImagenes()) {
            try {
                evidencias.add(servicio.guardar(archivo));
            } catch (EvidenciaInvalidaException ex) {
                errores.add(archivo.getFileName() + ": " + ex.getMessage());
            }
        }
        refrescarPanel();
        mostrarErrores(errores);
    }

    private void onQuitarSolicitado(ActionEvent e) {
        quitar(panel.getEvidenciaSeleccionada());
    }

    private void onAbrirImagenSolicitada(String nombre) {
        try {
            panel.mostrarImagenCompleta(servicio.leerImagen(nombre));
        } catch (EvidenciaInvalidaException ex) {
            panel.mostrarError(ex.getMessage());
        }
    }

    private void refrescarPanel() {
        Map<String, BufferedImage> imagenes = new LinkedHashMap<>();
        for (String nombre : evidencias) {
            imagenes.put(nombre, leerImagenOpcional(nombre));
        }
        panel.mostrarEvidencias(imagenes);
    }

    private BufferedImage leerImagenOpcional(String nombre) {
        try {
            return servicio.leerImagen(nombre);
        } catch (EvidenciaInvalidaException ex) {
            return null;
        }
    }

    private List<String> eliminarArchivos(List<String> nombres) {
        List<String> errores = new ArrayList<>();
        for (String nombre : nombres) {
            try {
                servicio.eliminar(nombre);
            } catch (EvidenciaInvalidaException ex) {
                errores.add(ex.getMessage());
            }
        }
        return errores;
    }

    private void mostrarErrores(List<String> errores) {
        if (!errores.isEmpty()) {
            panel.mostrarError(String.join("\n", errores));
        }
    }
}
