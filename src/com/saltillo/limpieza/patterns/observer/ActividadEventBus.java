package com.saltillo.limpieza.patterns.observer;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Bus desacoplado que implementa el patrón Observer (ISubject).
 * Actúa como intermediario central para el ciclo de vida de actividades de limpieza.
 * 
 * =========================================================================
 * CONTRATO PARA EL INTEGRANTE 3 (Angel - Issue #14: Publicar eventos):
 * Cuando Angel cree, actualice o elimine una actividad, simplemente invoca:
 * 
 * ActividadEventBus.getInstance().publicarEvento(
 *     new ActividadEventDTO(idCuadrilla, nombreCuadrilla, colonias, tipo, notas, tipoEvento)
 * );
 * =========================================================================
 */
public class ActividadEventBus implements ISubject<ActividadEventDTO> {

    private static ActividadEventBus instance;
    private final List<IObserver<ActividadEventDTO>> observers;

    private ActividadEventBus() {
        this.observers = new ArrayList<>();
    }

    /**
     * Acceso Singleton sincronizado al bus de eventos.
     */
    public static synchronized ActividadEventBus getInstance() {
        if (instance == null) {
            instance = new ActividadEventBus();
        }
        return instance;
    }

    @Override
    public synchronized void attach(IObserver<ActividadEventDTO> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void detach(IObserver<ActividadEventDTO> observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers(ActividadEventDTO event) {
        for (IObserver<ActividadEventDTO> observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception ex) {
                System.err.println("[ActividadEventBus] Error al notificar observador: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * Publica cualquier tipo de evento de actividad (creada, actualizada, eliminada).
     * 
     * @param event DTO con la información y tipo del evento.
     */
    public void publicarEvento(ActividadEventDTO event) {
        notifyObservers(event);
    }

    /**
     * Método semántico de conveniencia para publicar eventos de creación (retrocompatibilidad).
     * 
     * @param event DTO con la información de la nueva actividad.
     */
    public void publicarNuevaActividad(ActividadEventDTO event) {
        publicarEvento(event);
    }
}
