package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.TipoEventoActividad;
import com.saltillo.limpieza.models.entities.Colonia;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.repositories.IActividadRepository;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.IEvidenciaRepository;
import com.saltillo.limpieza.repositories.RepositorioException;
import com.saltillo.limpieza.services.EvidenciaInvalidaException;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;

import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del CRUD de Actividades (Integrante 3 - Issues #10 y #14).
 *
 * Arquitectura MVC Estricta: las vistas solo hablan con este controlador, que
 * valida los datos del formulario y coordina repositorios y evidencias.
 *
 * Patrón Observer: tras crear, actualizar o eliminar con éxito publica un evento en
 * ActividadEventBus. No conoce a quién le llega (Dashboard, Consulta u otros).
 */
public class ActividadController {

    private static final int SIN_ACTIVIDAD = 0;
    private static final String ESTADO_COMPLETADA = "COMPLETADA";
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final ActividadListView listView;
    private final ActividadFormView formView;
    private final IActividadRepository actividadRepository;
    private final IEvidenciaRepository evidenciaRepository;
    private final IDashboardRepository catalogoCuadrillas; // TODO(#3): cambiar por ICuadrillaRepository
    private final EvidenciaStorageService evidenciaService;
    private final EvidenciaController evidenciaController;
    private final ColoniaSelectorController coloniaSelector;

    private int idEnEdicion = SIN_ACTIVIDAD;
    private List<Colonia> coloniasEnEdicion = new ArrayList<>();

    public ActividadController(ActividadListView listView, ActividadFormView formView,
                               IActividadRepository actividadRepository, IEvidenciaRepository evidenciaRepository,
                               IDashboardRepository catalogoCuadrillas, EvidenciaStorageService evidenciaService,
                               ColoniaSelectorController coloniaSelector) {
        this.listView = listView;
        this.formView = formView;
        this.actividadRepository = actividadRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.catalogoCuadrillas = catalogoCuadrillas;
        this.evidenciaService = evidenciaService;
        this.evidenciaController = new EvidenciaController(formView.getEvidenciaPanel(), evidenciaService);
        this.coloniaSelector = coloniaSelector;

        this.formView.poblarCuadrillas(catalogoCuadrillas.obtenerNombresCuadrillas());
        this.formView.poblarTipos(actividadRepository.listarTiposActividad());

        this.listView.setNuevoListener(this::onNuevoSolicitado);
        this.listView.setEditarListener(this::onEditarSolicitado);
        this.listView.setEliminarListener(this::onEliminarSolicitado);
        this.listView.setFiltroEstadoListener(this::onFiltroCambiado);
        this.formView.setGuardarListener(this::onGuardarSolicitado);
        this.formView.setCancelarListener(this::onCancelarSolicitado);
        this.formView.setElegirColoniasListener(this::onElegirColoniasSolicitado);
        this.formView.setQuitarColoniasListener(this::onQuitarColoniasSolicitado);

        refrescarLista();
    }

    public void nuevaActividad() {
        idEnEdicion = SIN_ACTIVIDAD;
        coloniasEnEdicion = new ArrayList<>();
        formView.limpiar();
        formView.mostrarColonias(coloniasEnEdicion);
        evidenciaController.cargar(List.of());
        formView.abrir(listView, "Nueva actividad");
    }

    public void editarActividad(int idActividad) {
        ActividadDetalleDTO actividad = actividadRepository.buscarPorId(idActividad);
        if (actividad == null) {
            listView.mostrarError("La actividad seleccionada ya no existe.");
            refrescarLista();
            return;
        }

        idEnEdicion = idActividad;
        coloniasEnEdicion = new ArrayList<>(coloniaSelector.resolverNombres(actividad.getColonias()));
        formView.mostrarColonias(coloniasEnEdicion);
        formView.mostrarDatos(actividad.getNombreCuadrilla(), actividad.getTipoLimpieza(), actividad.getLugarEspecifico(),
                actividad.getFechaHoraFormateada(), actividad.getFechaRealizacionFormateada(),
                actividad.getEstado(), actividad.getObservaciones());
        evidenciaController.cargar(evidenciaRepository.listarPorActividad(idActividad));
        formView.abrir(listView, "Editar actividad #" + idActividad);
    }

    public void guardar() {
        List<String> errores = new ArrayList<>();
        String cuadrilla = formView.getCuadrilla();
        String tipo = formView.getTipo();
        String lugar = formView.getLugar().trim();
        String estado = formView.getEstado();

        if (cuadrilla == null) errores.add("Selecciona una cuadrilla.");
        if (tipo == null) errores.add("Selecciona un tipo de actividad.");
        if (lugar.isEmpty()) errores.add("Escribe el lugar específico.");
        if (coloniasEnEdicion.isEmpty()) errores.add("Elige al menos una colonia atendida.");

        LocalDateTime programada = leerFecha(formView.getFechaProgramada(), "programada", errores);
        LocalDateTime realizada = leerFecha(formView.getFechaRealizacion(), "de realización", errores);
        if (formView.getFechaProgramada().isBlank()) errores.add("Escribe la fecha programada.");
        if (ESTADO_COMPLETADA.equals(estado) && formView.getFechaRealizacion().isBlank()) {
            errores.add("Una actividad completada necesita fecha de realización.");
        }

        if (!errores.isEmpty()) {
            formView.mostrarError(String.join("\n", errores));
            return;
        }

        ActividadDetalleDTO actividad = new ActividadDetalleDTO(idEnEdicion, catalogoCuadrillas.obtenerIdPorNombre(cuadrilla),
                cuadrilla, coloniasEnEdicion.stream().map(Colonia::getNombre).toList(), tipo, lugar, estado, programada, formView.getDescripcion().trim(), realizada);

        boolean esNueva = idEnEdicion == SIN_ACTIVIDAD;
        try {
            int idActividad = esNueva ? actividadRepository.guardar(actividad) : idEnEdicion;
            if (!esNueva) {
                actividadRepository.actualizar(actividad);
            }
            evidenciaRepository.reemplazar(idActividad, evidenciaController.getEvidencias());
        } catch (RepositorioException ex) {
            formView.mostrarError("No se pudo guardar la actividad. Intenta de nuevo.");
            return;
        }
        evidenciaController.confirmarCambios();

        formView.cerrar();
        refrescarLista();
        publicarEvento(actividad, esNueva ? TipoEventoActividad.CREADA : TipoEventoActividad.ACTUALIZADA);
    }

    public void elegirColonias() {
        coloniaSelector.seleccionar(formView, coloniasEnEdicion).ifPresent(elegidas -> {
            coloniasEnEdicion = new ArrayList<>(elegidas);
            formView.mostrarColonias(coloniasEnEdicion);
        });
    }

    public void quitarColonias(List<Colonia> colonias) {
        coloniasEnEdicion.removeIf(c -> colonias.stream().anyMatch(m -> ColoniaSelectorController.mismaColonia(c, m)));
        formView.mostrarColonias(coloniasEnEdicion);
    }

    public void cancelar() {
        evidenciaController.descartarCambios();
        formView.cerrar();
    }

    public void eliminarActividad(int idActividad) {
        // Los datos se toman antes de borrar: después ya no existen para armar el evento.
        ActividadDetalleDTO eliminada = actividadRepository.buscarPorId(idActividad);
        if (eliminada == null) {
            listView.mostrarError("La actividad seleccionada ya no existe.");
            refrescarLista();
            return;
        }
        if (!listView.confirmarEliminacion("¿Eliminar la actividad #" + idActividad + " y sus evidencias?")) {
            return;
        }

        List<String> evidencias = evidenciaRepository.listarPorActividad(idActividad);
        try {
            actividadRepository.eliminar(idActividad);
            evidenciaRepository.eliminarPorActividad(idActividad);
        } catch (RepositorioException ex) {
            listView.mostrarError("No se pudo eliminar la actividad. Intenta de nuevo.");
            return;
        }
        refrescarLista();
        eliminarArchivos(evidencias);
        publicarEvento(eliminada, TipoEventoActividad.ELIMINADA);
    }

    public void refrescarLista() {
        listView.mostrarActividades(actividadRepository.listar(listView.getFiltroEstado()));
    }

    private void onNuevoSolicitado(ActionEvent e) {
        nuevaActividad();
    }

    private void onEditarSolicitado(ActionEvent e) {
        Integer id = listView.getIdSeleccionado();
        if (id == null) {
            listView.mostrarError("Selecciona una actividad para editarla.");
            return;
        }
        editarActividad(id);
    }

    private void onEliminarSolicitado(ActionEvent e) {
        Integer id = listView.getIdSeleccionado();
        if (id == null) {
            listView.mostrarError("Selecciona una actividad para eliminarla.");
            return;
        }
        eliminarActividad(id);
    }

    private void onFiltroCambiado(ActionEvent e) {
        refrescarLista();
    }

    private void onGuardarSolicitado(ActionEvent e) {
        guardar();
    }

    private void onCancelarSolicitado(ActionEvent e) {
        cancelar();
    }

    private void onElegirColoniasSolicitado(ActionEvent e) {
        elegirColonias();
    }

    private void onQuitarColoniasSolicitado(ActionEvent e) {
        quitarColonias(formView.getColoniasMarcadas());
    }

    private void publicarEvento(ActividadDetalleDTO actividad, TipoEventoActividad tipoEvento) {
        ActividadEventBus.getInstance().publicarEvento(new ActividadEventDTO(
                actividad.getIdCuadrilla(),
                actividad.getNombreCuadrilla(),
                actividad.getColonias(),
                actividad.getTipoLimpieza(),
                actividad.getObservaciones(),
                tipoEvento));
    }

    private LocalDateTime leerFecha(String texto, String etiqueta, List<String> errores) {
        if (texto.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            errores.add("La fecha " + etiqueta + " debe tener el formato dd/mm/aaaa hh:mm.");
            return null;
        }
    }

    private void eliminarArchivos(List<String> nombres) {
        List<String> errores = new ArrayList<>();
        for (String nombre : nombres) {
            try {
                evidenciaService.eliminar(nombre);
            } catch (EvidenciaInvalidaException ex) {
                errores.add(ex.getMessage());
            }
        }
        if (!errores.isEmpty()) {
            listView.mostrarError(String.join("\n", errores));
        }
    }
}
