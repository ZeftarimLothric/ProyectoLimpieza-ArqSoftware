package com.saltillo.limpieza.patterns.observer;

/**
 * Interfaz genérica para el rol de Sujeto (Observable) en el patrón Observer.
 * Define las operaciones fundamentales para registrar, desuscribir y notificar observadores.
 *
 * @param <T> Tipo de evento gestionado por el sujeto
 */
public interface ISubject<T> {
    /**
     * Suscribe un observador a la lista de notificaciones.
     * 
     * @param observer Observador interesado en recibir eventos
     */
    void attach(IObserver<T> observer);

    /**
     * Remueve un observador previamente suscrito.
     * 
     * @param observer Observador a desuscribir
     */
    void detach(IObserver<T> observer);

    /**
     * Notifica a todos los observadores registrados enviándoles el evento.
     * 
     * @param event Instancia del evento ocurrido
     */
    void notifyObservers(T event);
}
