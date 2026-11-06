package com.saltillo.limpieza.patterns.observer;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Bus desacoplado que implementa el patrón Observer (ISubject).
 * Actúa como intermediario central de eventos de actividades de limpieza.
 * 
 * =========================================================================
 * CONEXIÓN PARA EL INTEGRANTE 3 (Operaciones & Evidencias):
 * Cuando el Integrante 3 guarde una nueva actividad en su módulo, simplemente
 * debe ejecutar la siguiente línea de código para que el Dashboard se actualice:
 * 
 * ActividadEventBus.getInstance().publicarNuevaActividad(
 *     new ActividadEventDTO(idCuadrilla, nombreCuadrilla, colonia, tipoLimpieza, notas)
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
     * Método semántico de conveniencia para publicar eventos de nuevas actividades.
     * 
     * @param event DTO con la información de la actividad recién registrada.
     */
    public void publicarNuevaActividad(ActividadEventDTO event) {
        notifyObservers(event);
    }
}
