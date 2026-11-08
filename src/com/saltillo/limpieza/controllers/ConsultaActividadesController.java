package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.FiltroActividadDTO;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.patterns.observer.IObserver;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.views.ConsultaActividadesView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Controlador de Consulta y Filtrado de Actividades (Integrante 4 - Issue #16).
 * 
 * Implementa:
 * 1. Arquitectura MVC Estricta: Orquesta la vista de consulta con el repositorio.
 * 2. Patrón Observer (IObserver): Suscrito al ActividadEventBus para refrescar
 *    la tabla automáticamente si se registra o modifica una actividad en otro módulo.
 */
public class ConsultaActividadesController implements IObserver<ActividadEventDTO> {

    private final ConsultaActividadesView view;
    private final IDashboardRepository repository;

    public ConsultaActividadesController(ConsultaActividadesView view, IDashboardRepository repository) {
        this.view = view;
        this.repository = repository;

        // 1. Suscribirse al Patrón Observer
        ActividadEventBus.getInstance().attach(this);

        // 2. Poblar catálogos en la vista
        this.view.poblarCuadrillas(this.repository.obtenerNombresCuadrillas());

        // 3. Vincular listeners de interfaz (Strict MVC)
        this.view.setBuscarListener(this::onBuscarSolicitado);
        this.view.setLimpiarListener(this::onLimpiarSolicitado);

        // 4. Búsqueda inicial para cargar el histórico
        ejecutarBusqueda();
    }

    /**
     * Recupera los filtros seleccionados, consulta el repositorio y entrega los DTOs a la vista.
     */
    public void ejecutarBusqueda() {
        FiltroActividadDTO filtro = view.obtenerFiltroActual(repository::obtenerIdPorNombre);
        List<ActividadDetalleDTO> resultados = repository.buscarActividades(filtro);
        view.mostrarResultados(resultados);
    }

    private void onBuscarSolicitado(ActionEvent e) {
        ejecutarBusqueda();
    }

    private void onLimpiarSolicitado(ActionEvent e) {
        view.limpiarFiltros();
        ejecutarBusqueda();
    }

    /**
     * PATRÓN OBSERVER:
     * Si el Integrante 3 o el simulador registran una nueva actividad,
     * la consulta se actualiza en tiempo real sin recargar ventanas.
     */
    @Override
    public void onEvent(ActividadEventDTO event) {
        SwingUtilities.invokeLater(this::ejecutarBusqueda);
    }

    public void destruir() {
        ActividadEventBus.getInstance().detach(this);
    }
}
