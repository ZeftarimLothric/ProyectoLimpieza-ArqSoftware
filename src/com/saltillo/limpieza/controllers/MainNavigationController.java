package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.MainShellView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Principal de Navegación y Gestión de Sesión.
 * 
 * Cumplimiento de Contratos Issue #4, #15 y #16:
 * - Se integra con SesionUsuario (Singleton).
 * - Control de enrutamiento para Dashboard, Consulta y módulos externos.
 * - Cierre seguro de sesión destruyendo la instancia de sesión en memoria,
 *   desuscribiendo observadores, ejecutando limpieza de recursos y manejando el cierre por ventana (X).
 */
public class MainNavigationController {

    private final MainShellView shellView;
    private final List<Runnable> tareasLimpieza = new ArrayList<>();
    private Runnable alCerrarSesionCallback;

    public MainNavigationController(MainShellView shellView) {
        this.shellView = shellView;

        // Inicializar sesión por defecto si no existe una previa
        if (!SesionUsuario.getInstance().estaActiva()) {
            Usuario demoUser = new Usuario(1, "Lic. Administrador Municipal", "admin.saltillo@municipio.gob", "ADMINISTRADOR");
            SesionUsuario.getInstance().iniciar(demoUser);
        }

        actualizarVistaUsuario();

        // Vincular eventos del menú lateral y ventana hacia este controlador (Strict MVC)
        this.shellView.setNavDashboardListener(e -> navegarA("DASHBOARD"));
        this.shellView.setNavConsultaListener(e -> navegarA("CONSULTA"));
        this.shellView.setNavActividadesListener(e -> navegarA("ACTIVIDADES"));
        this.shellView.setNavCuadrillasListener(e -> navegarA("CUADRILLAS"));
        this.shellView.setNavColoniasListener(e -> navegarA("COLONIAS"));
        this.shellView.setCerrarSesionListener(this::onCerrarSesionSolicitado);
        this.shellView.setCerrarVentanaListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCerrarVentanaSolicitado();
            }
        });
    }

    /**
     * Registra una tarea de limpieza (desuscribir observadores, cerrar conexión BD, etc.)
     * que se ejecutará al cerrar sesión o cerrar la ventana con la X.
     */
    public void registrarLimpiezaAlCerrar(Runnable tarea) {
        if (tarea != null && !tareasLimpieza.contains(tarea)) {
            tareasLimpieza.add(tarea);
        }
    }

    /**
     * Configura el callback opcional para regresar al Login al cerrar sesión (Issue #7 / #15).
     */
    public void setAlCerrarSesionCallback(Runnable callback) {
        this.alCerrarSesionCallback = callback;
    }

    public void actualizarVistaUsuario() {
        Usuario u = SesionUsuario.getInstance().getUsuarioActual();
        if (u != null) {
            this.shellView.setUsuarioSesion(u.getUsuario());
        } else {
            this.shellView.setUsuarioSesion("Sin sesión");
        }
    }

    /**
     * Enruta el Content Host hacia el módulo solicitado.
     */
    public void navegarA(String claveModulo) {
        if (!SesionUsuario.getInstance().estaActiva()) {
            JOptionPane.showMessageDialog(shellView,
                    "Acceso denegado. No existe una sesión activa.",
                    "Seguridad del Sistema",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        shellView.mostrarModulo(claveModulo);
    }

    /**
     * REQUISITO 3 DEL PROFESOR & ISSUE #15:
     * Cierre seguro de sesión: destruye variables de sesión mediante SesionUsuario,
     * limpia memoria y finaliza recursos.
     */
    private void onCerrarSesionSolicitado(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(shellView,
                "¿Está seguro de que desea cerrar la sesión actual?\n" +
                "(Se destruirán las variables de sesión en memoria, se quitarán los observadores y se cerrarán recursos).",
                "Confirmación de Cierre Seguro",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            destruirSesionYSeguridad(false);
        }
    }

    private void onCerrarVentanaSolicitado() {
        int confirm = JOptionPane.showConfirmDialog(shellView,
                "¿Está seguro de que desea salir del sistema?\n" +
                "(Se destruirá la sesión activa y se cerrarán los recursos de forma segura).",
                "Confirmación de Salida Segura",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            destruirSesionYSeguridad(true);
        }
    }

    /**
     * Ejecuta la limpieza de recursos registrados (observadores, conexión BD)
     * y destruye la sesión activa sin cerrar el proceso JVM (apto para pruebas y callbacks).
     */
    public void ejecutarLimpiezaRecursos() {
        Usuario u = SesionUsuario.getInstance().getUsuarioActual();
        String nombreUser = (u != null) ? u.getUsuario() : "Desconocido";

        System.out.println("[Seguridad] Destruyendo sesión y liberando recursos para: " + nombreUser);

        for (Runnable tarea : tareasLimpieza) {
            try {
                tarea.run();
            } catch (Exception ex) {
                System.err.println("[Seguridad] Error en tarea de limpieza: " + ex.getMessage());
            }
        }

        SesionUsuario.getInstance().destruir();
    }

    /**
     * Destruye de forma estricta la sesión global, libera observadores/recursos y cierra el Shell.
     */
    public void destruirSesionYSeguridad() {
        destruirSesionYSeguridad(false);
    }

    private void destruirSesionYSeguridad(boolean forzarSalidaProceso) {
        ejecutarLimpiezaRecursos();

        JOptionPane.showMessageDialog(shellView,
                "Sesión cerrada correctamente.\n" +
                "Variables de seguridad destruidas mediante SesionUsuario y recursos liberados.\n" +
                "¡Hasta pronto!",
                "Sesión Finalizada",
                JOptionPane.INFORMATION_MESSAGE);

        shellView.dispose();
        if (!forzarSalidaProceso && alCerrarSesionCallback != null) {
            alCerrarSesionCallback.run();
        } else {
            System.exit(0);
        }
    }

    public boolean isSesionAutenticada() {
        return SesionUsuario.getInstance().estaActiva();
    }
}
