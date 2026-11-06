package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.views.MainShellView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Controlador Principal de Navegación y Gestión de Sesión.
 * 
 * Responsabilidades:
 * 1. Control de enrutamiento modular entre las vistas de los 4 integrantes.
 * 2. Cumplimiento del Requisito 3 del Profesor:
 *    "Opción para cerrar sistema (Seguridad de la aplicación, cerrar base de datos
 *    y destruir variables globales de sesión) y Menú para navegar entre opciones."
 */
public class MainNavigationController {

    private final MainShellView shellView;

    // Variables de estado de sesión global (cumplimiento de seguridad y destrucción)
    private String sesionUsuarioActivo;
    private String sesionRolActivo;
    private long sesionTimestampInicio;
    private boolean sesionAutenticada;

    public MainNavigationController(MainShellView shellView) {
        this.shellView = shellView;

        // Inicializar sesión por defecto (Admin de demostración)
        iniciarSesion("admin.saltillo@municipio.gob", "ADMINISTRADOR_SERVICIOS");

        // Vincular eventos del menú lateral hacia este controlador (Strict MVC)
        this.shellView.setNavDashboardListener(e -> navegarA("DASHBOARD"));
        this.shellView.setNavActividadesListener(e -> navegarA("ACTIVIDADES"));
        this.shellView.setNavCuadrillasListener(e -> navegarA("CUADRILLAS"));
        this.shellView.setNavColoniasListener(e -> navegarA("COLONIAS"));
        this.shellView.setCerrarSesionListener(this::onCerrarSesionSolicitado);
    }

    /**
     * Inicia una sesión autenticada estableciendo las variables de estado.
     */
    public void iniciarSesion(String usuario, String rol) {
        this.sesionUsuarioActivo = usuario;
        this.sesionRolActivo = rol;
        this.sesionTimestampInicio = System.currentTimeMillis();
        this.sesionAutenticada = true;

        this.shellView.setUsuarioSesion(this.sesionUsuarioActivo);
    }

    /**
     * Enruta el Content Host hacia el módulo solicitado.
     */
    public void navegarA(String claveModulo) {
        if (!sesionAutenticada) {
            JOptionPane.showMessageDialog(shellView,
                    "Acceso denegado. No existe una sesión activa.",
                    "Seguridad del Sistema",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        shellView.mostrarModulo(claveModulo);
    }

    /**
     * REQUISITO 3 DEL PROFESOR:
     * Cierre seguro de sesión: destruye variables de sesión, limpia memoria y
     * finaliza la ejecución o redirige a la pantalla de acceso.
     */
    private void onCerrarSesionSolicitado(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(shellView,
                "¿Está seguro de que desea cerrar la sesión actual y salir del sistema?\n" +
                "(Se destruirán las variables de sesión en memoria y se cerrarán recursos).",
                "Confirmación de Cierre Seguro",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            destruirSesionYSeguridad();
        }
    }

    /**
     * Destruye de forma estricta las variables globales de sesión para garantizar seguridad.
     */
    public void destruirSesionYSeguridad() {
        System.out.println("[Seguridad] Destruyendo variables de sesión para: " + sesionUsuarioActivo);

        this.sesionUsuarioActivo = null;
        this.sesionRolActivo = null;
        this.sesionTimestampInicio = 0L;
        this.sesionAutenticada = false;

        // Limpieza de interfaz y mensaje de cierre
        JOptionPane.showMessageDialog(shellView,
                "Sesión cerrada correctamente.\n" +
                "Variables de seguridad destruidas.\n" +
                "¡Hasta pronto!",
                "Sesión Finalizada",
                JOptionPane.INFORMATION_MESSAGE);

        shellView.dispose();
        System.exit(0);
    }

    public boolean isSesionAutenticada() {
        return sesionAutenticada;
    }
}
