package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.CuadrillaResumenDTO;
import com.saltillo.limpieza.models.dto.DashboardKpisDTO;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.patterns.observer.IObserver;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.views.DashboardView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Random;

/**
 * Controlador del Dashboard Ejecutivo (Integrante 4).
 * 
 * Implementa:
 * 1. Arquitectura MVC estricta: Orquesta los modelos/DTOs, el repositorio y la vista.
 * 2. Patrón de Diseño Observer (IObserver): Suscrito al 'ActividadEventBus'. Cuando
 *    cualquier módulo (o simulación) genera una nueva actividad, el controlador es
 *    notificado automáticamente y refresca las métricas sin necesidad de recargar
 *    la página ni hacer polling.
 */
public class DashboardController implements IObserver<ActividadEventDTO> {

    private final DashboardView view;
    private final IDashboardRepository repository;
    private final Random random = new Random();

    // Colonias de Saltillo para la simulación rápida del profesor
    private final String[] coloniasSaltillo = {
            "Centro Histórico", "República Poniente", "Mirasierra", "Saltillo 2000",
            "Lomas de Lourdes", "San Patricio", "Doctores", "Cumbres", "Valle de las Flores"
    };

    private final String[] tiposLimpieza = {
            "Barrido Manual y Desazolve",
            "Deshierbe y Poda de Camellones",
            "Retiro de Escombros y Enseres",
            "Lavado de Espacios Públicos",
            "Limpieza de Canales Pluviales"
    };

    public DashboardController(DashboardView view, IDashboardRepository repository) {
        this.view = view;
        this.repository = repository;

        // 1. Suscripción al Patrón Observer
        ActividadEventBus.getInstance().attach(this);

        // 2. Vinculación de eventos de la vista (Strict MVC)
        this.view.setRefrescarListener(this::onRefrescarSolicitado);
        this.view.setSimularActividadListener(this::onSimularActividad);

        // 3. Carga inicial de datos
        refrescarMetricas();
    }

    /**
     * Consulta el repositorio y actualiza los componentes de la vista.
     */
    public void refrescarMetricas() {
        DashboardKpisDTO kpis = repository.obtenerKpis();
        List<CuadrillaResumenDTO> cuadrillas = repository.obtenerResumenPorCuadrillas();

        // Enviar DTOs limpios a la vista
        view.mostrarKpis(kpis);
        view.mostrarResumenCuadrillas(cuadrillas);
    }

    private void onRefrescarSolicitado(ActionEvent e) {
        refrescarMetricas();
    }

    /**
     * Acción ejecutada al presionar el botón de prueba "Simular Actividad".
     * Modifica el repositorio y genera el evento Observer.
     */
    private void onSimularActividad(ActionEvent e) {
        List<String> nombres = repository.obtenerNombresCuadrillas();
        if (nombres.isEmpty()) return;

        // Elegir aleatoriamente una cuadrilla, colonias y tipo de servicio de Saltillo
        String cuadrillaElegida = nombres.get(random.nextInt(nombres.size()));
        int idCuadrilla = repository.obtenerIdPorNombre(cuadrillaElegida);
        String col1 = coloniasSaltillo[random.nextInt(coloniasSaltillo.length)];
        List<String> colonias = new java.util.ArrayList<>();
        colonias.add(col1);
        if (random.nextBoolean()) {
            String col2 = coloniasSaltillo[random.nextInt(coloniasSaltillo.length)];
            if (!col2.equalsIgnoreCase(col1)) {
                colonias.add(col2);
            }
        }

        String tipo = tiposLimpieza[random.nextInt(tiposLimpieza.length)];
        String notas = "Operativo extraordinario de supervisión municipal.";

        // El repositorio guarda y emite el evento al ActividadEventBus
        repository.registrarActividad(idCuadrilla, colonias, tipo, notas);
    }

    /**
     * =========================================================================
     * PATRÓN OBSERVER: Método de notificación reactiva
     * =========================================================================
     * Cuando el 'ActividadEventBus' notifica que una actividad fue registrada
     * (ya sea por el Integrante 3 o por una simulación), este método se dispara
     * de forma inmediata.
     */
    @Override
    public void onEvent(ActividadEventDTO event) {
        // Garantizar ejecución en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(() -> {
            // 1. Mostrar notificación en el Feed en vivo
            view.agregarEventoObserver(event);

            // 2. Recalcular KPIs y actualizar la tabla con datos frescos
            refrescarMetricas();
        });
    }

    /**
     * Permite desuscribir el observador si la vista se destruye.
     */
    public void destruir() {
        ActividadEventBus.getInstance().detach(this);
    }
}
