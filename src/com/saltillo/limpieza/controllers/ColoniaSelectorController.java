package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.dto.ColoniaDTO;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Controlador del selector de colonias atendidas (Integrante 3 - Issue #11).
 *
 * Recibe la búsqueda como función para no depender todavía de un repositorio concreto.
 * TODO(#3): pasar coloniaRepository::buscarPorNombreOCodigoPostal cuando exista IColoniaRepository.
 */
public class ColoniaSelectorController {

    private final ColoniaSelectorDialog dialog;
    private final Function<String, List<ColoniaDTO>> buscador;
    private List<ColoniaDTO> seleccionadas = new ArrayList<>();
    private List<ColoniaDTO> confirmadas;

    public ColoniaSelectorController(ColoniaSelectorDialog dialog, Function<String, List<ColoniaDTO>> buscador) {
        this.dialog = dialog;
        this.buscador = buscador;

        this.dialog.setBuscarListener(this::onBuscarSolicitado);
        this.dialog.setAgregarListener(this::onAgregarSolicitado);
        this.dialog.setQuitarListener(this::onQuitarSolicitado);
        this.dialog.setAceptarListener(this::onAceptarSolicitado);
        this.dialog.setCancelarListener(this::onCancelarSolicitado);
    }

    /**
     * Abre el diálogo con las colonias ya elegidas y espera a que el usuario termine.
     *
     * @return las colonias confirmadas, o vacío si el usuario canceló
     */
    public Optional<List<ColoniaDTO>> seleccionar(Component padre, List<ColoniaDTO> actuales) {
        seleccionadas = actuales != null ? new ArrayList<>(actuales) : new ArrayList<>();
        confirmadas = null;

        dialog.limpiarBusqueda();
        buscar("");
        dialog.mostrarSeleccionadas(seleccionadas);
        dialog.abrir(padre);
        return Optional.ofNullable(confirmadas);
    }

    /**
     * Recupera las colonias de una actividad guardada, que hoy solo trae sus nombres.
     * Si un nombre ya no está en el catálogo se conserva sin código postal para no perderlo.
     *
     * TODO(#18): sobra cuando las actividades guarden el ID de cada colonia.
     */
    public List<ColoniaDTO> resolverNombres(List<String> nombres) {
        List<ColoniaDTO> colonias = new ArrayList<>();
        for (String nombre : nombres) {
            colonias.add(buscador.apply(nombre).stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                    .findFirst()
                    .orElse(new ColoniaDTO(-(colonias.size() + 1), nombre, "?")));
        }
        return colonias;
    }

    public void buscar(String texto) {
        dialog.mostrarResultados(buscador.apply(texto.trim()));
    }

    public void agregar(List<ColoniaDTO> colonias) {
        for (ColoniaDTO colonia : colonias) {
            if (!seleccionadas.contains(colonia)) {
                seleccionadas.add(colonia);
            }
        }
        dialog.mostrarSeleccionadas(seleccionadas);
    }

    public void quitar(List<ColoniaDTO> colonias) {
        seleccionadas.removeAll(colonias);
        dialog.mostrarSeleccionadas(seleccionadas);
    }

    public void aceptar() {
        if (seleccionadas.isEmpty()) {
            dialog.mostrarError("Selecciona al menos una colonia.");
            return;
        }
        confirmadas = List.copyOf(seleccionadas);
        dialog.cerrar();
    }

    public void cancelar() {
        confirmadas = null;
        dialog.cerrar();
    }

    private void onBuscarSolicitado(ActionEvent e) {
        buscar(dialog.getTextoBusqueda());
    }

    private void onAgregarSolicitado(ActionEvent e) {
        agregar(dialog.getResultadosMarcados());
    }

    private void onQuitarSolicitado(ActionEvent e) {
        quitar(dialog.getSeleccionadasMarcadas());
    }

    private void onAceptarSolicitado(ActionEvent e) {
        aceptar();
    }

    private void onCancelarSolicitado(ActionEvent e) {
        cancelar();
    }
}
