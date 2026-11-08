package com.saltillo.limpieza.session;

import com.saltillo.limpieza.models.entities.Usuario;

/**
 * Gestor global de sesión de usuario bajo el patrón de diseño Singleton.
 * 
 * Contrato central para:
 * - Integrante 2 (Eli - Issue #7): Al autenticar con éxito en el Login, invoca:
 *     SesionUsuario.getInstance().iniciar(usuarioAutenticado);
 * - Integrante 4 (Luis Eduardo - Issue #15): Al cerrar sesión segura o salir, invoca:
 *     SesionUsuario.getInstance().destruir();
 */
public class SesionUsuario {

    private static SesionUsuario instance;
    private Usuario usuarioActual;
    private long timestampInicio;

    private SesionUsuario() {
        this.usuarioActual = null;
        this.timestampInicio = 0L;
    }

    /**
     * Acceso Singleton sincronizado para control de concurrencia.
     */
    public static synchronized SesionUsuario getInstance() {
        if (instance == null) {
            instance = new SesionUsuario();
        }
        return instance;
    }

    /**
     * Inicia una sesión activa con el usuario autenticado.
     * 
     * @param usuario Instancia del usuario que inició sesión.
     */
    public synchronized void iniciar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("No se puede iniciar sesión con un usuario nulo.");
        }
        this.usuarioActual = usuario;
        this.timestampInicio = System.currentTimeMillis();
        System.out.println("[SesionUsuario] Sesión iniciada para: " + usuario.getUsuario() + " (" + usuario.getRol() + ")");
    }

    /**
     * Obtiene el usuario autenticado actualmente en el sistema.
     * 
     * @return Usuario autenticado o null si no hay sesión activa.
     */
    public synchronized Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Verifica si existe una sesión activa actualmente en memoria.
     * 
     * @return true si hay un usuario autenticado, false en caso contrario.
     */
    public synchronized boolean estaActiva() {
        return usuarioActual != null;
    }

    /**
     * Destruye de forma segura la sesión actual en memoria (Requisito 3 de rúbrica).
     * Anula el usuario actual y resetea los timestamps.
     */
    public synchronized void destruir() {
        if (usuarioActual != null) {
            System.out.println("[SesionUsuario] Destruyendo sesión en memoria para: " + usuarioActual.getUsuario());
        }
        this.usuarioActual = null;
        this.timestampInicio = 0L;
    }

    /**
     * Retorna el timestamp en milisegundos en el que inició la sesión.
     */
    public synchronized long getTimestampInicio() {
        return timestampInicio;
    }
}
