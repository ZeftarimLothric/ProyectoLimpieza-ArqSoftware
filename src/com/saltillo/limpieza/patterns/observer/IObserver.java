package com.saltillo.limpieza.patterns.observer;

/**
 * Interfaz genérica para el rol de Observador dentro del patrón de diseño Observer.
 * 
 * Permite a cualquier componente (ej. DashboardController) escuchar eventos de
 * dominio sin acoplarse directamente a la fuente emisora.
 *
 * @param <T> Tipo de datos o evento transportado
 */
@FunctionalInterface
public interface IObserver<T> {
    /**
     * Método invocado cuando el Sujeto notifica un cambio o evento.
     * 
     * @param event Información del evento recibido
     */
    void onEvent(T event);
}
