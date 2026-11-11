package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.entities.Colonia;
import com.saltillo.limpieza.repositories.IColoniaRepository;
import com.saltillo.limpieza.repositories.RepositorioException;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador del selector de colonias atendidas (Integrante 3 - Issue #11).
 *
 * Busca en IColoniaRepository: SqlColoniaRepository en la app y MockColoniaRepository en pruebas.
 */
public class ColoniaSelectorController {

    private final ColoniaSelectorDialog dialog;
    private final IColoniaRepository coloniaRepository;
    private List<Colonia> seleccionadas = new ArrayList<>();
    private List<Colonia> confirmadas;

    public ColoniaSelectorController(ColoniaSelectorDialog dialog, IColoniaRepository coloniaRepository) {
        this.dialog = dialog;
        this.coloniaRepository = coloniaRepository;

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
    public Optional<List<Colonia>> seleccionar(Component padre, List<Colonia> actuales) {
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
     * Si un nombre no está en el catálogo se conserva solo con el nombre para no perderlo.
     *
     * TODO(#18): sobra cuando las actividades guarden el ID de cada colonia.
     */
    public List<Colonia> resolverNombres(List<String> nombres) {
        List<Colonia> colonias = new ArrayList<>();
        for (String nombre : nombres) {
            colonias.add(buscarPorNombreExacto(nombre).orElseGet(() -> soloConNombre(nombre)));
        }
        return colonias;
    }

    public void buscar(String texto) {
        try {
            dialog.mostrarResultados(coloniaRepository.buscarPorNombreOCodigoPostal(texto.trim()));
        } catch (RepositorioException ex) {
            dialog.mostrarResultados(List.of());
            dialog.mostrarError("No se pudieron consultar las colonias. Revisa la conexión con la base de datos.");
        }
    }

    public void agregar(List<Colonia> colonias) {
        for (Colonia colonia : colonias) {
            if (!contiene(seleccionadas, colonia)) {
                seleccionadas.add(colonia);
            }
        }
        dialog.mostrarSeleccionadas(seleccionadas);
    }

    public void quitar(List<Colonia> colonias) {
        seleccionadas.removeIf(c -> contiene(colonias, c));
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

    /**
     * Colonia no define equals: cada búsqueda en la BD crea objetos nuevos, así que se comparan por ID,
     * o por nombre cuando alguna no tiene ID (las que solo se recuperaron por nombre).
     */
    public static boolean mismaColonia(Colonia a, Colonia b) {
        if (a.getIdColonia() > 0 && b.getIdColonia() > 0) {
            return a.getIdColonia() == b.getIdColonia();
        }
        return a.getNombre().equalsIgnoreCase(b.getNombre());
    }

    private static boolean contiene(List<Colonia> lista, Colonia colonia) {
        return lista.stream().anyMatch(c -> mismaColonia(c, colonia));
    }

    private Optional<Colonia> buscarPorNombreExacto(String nombre) {
        try {
            return coloniaRepository.buscarPorNombreOCodigoPostal(nombre).stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                    .findFirst();
        } catch (RepositorioException ex) {
            return Optional.empty();
        }
    }

    private Colonia soloConNombre(String nombre) {
        Colonia colonia = new Colonia();
        colonia.setNombre(nombre);
        return colonia;
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
