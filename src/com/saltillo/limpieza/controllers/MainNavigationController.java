package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.MainShellView;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Controlador Principal de Navegación y Gestión de Sesión.
 * 
 * Cumplimiento de Contratos Issue #4, #15 y #16:
 * - Se integra con SesionUsuario (Singleton).
 * - Control de enrutamiento para Dashboard, Consulta y módulos externos.
 * - Cierre seguro de sesión destruyendo la instancia de sesión en memoria.
 */
public class MainNavigationController {

    private final MainShellView shellView;

    public MainNavigationController(MainShellView shellView) {
        this.shellView = shellView;

        // Inicializar sesión por defecto si no existe una previa
        if (!SesionUsuario.getInstance().estaActiva()) {
            Usuario demoUser = new Usuario(1, "Lic. Administrador Municipal", "admin.saltillo@municipio.gob", "ADMINISTRADOR");
            SesionUsuario.getInstance().iniciar(demoUser);
        }

        actualizarVistaUsuario();

        // Vincular eventos del menú lateral hacia este controlador (Strict MVC)
        this.shellView.setNavDashboardListener(e -> navegarA("DASHBOARD"));
        this.shellView.setNavConsultaListener(e -> navegarA("CONSULTA"));
        this.shellView.setNavActividadesListener(e -> navegarA("ACTIVIDADES"));
        this.shellView.setNavCuadrillasListener(e -> navegarA("CUADRILLAS"));
        this.shellView.setNavColoniasListener(e -> navegarA("COLONIAS"));
        this.shellView.setCerrarSesionListener(this::onCerrarSesionSolicitado);
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
     * Destruye de forma estricta la sesión global en memoria.
     */
    public void destruirSesionYSeguridad() {
        Usuario u = SesionUsuario.getInstance().getUsuarioActual();
        String nombreUser = (u != null) ? u.getUsuario() : "Desconocido";

        System.out.println("[Seguridad] Destruyendo sesión en memoria para: " + nombreUser);
        SesionUsuario.getInstance().destruir();

        JOptionPane.showMessageDialog(shellView,
                "Sesión cerrada correctamente.\n" +
                "Variables de seguridad destruidas mediante SesionUsuario.\n" +
                "¡Hasta pronto!",
                "Sesión Finalizada",
                JOptionPane.INFORMATION_MESSAGE);

        shellView.dispose();
        System.exit(0);
    }

    public boolean isSesionAutenticada() {
        return SesionUsuario.getInstance().estaActiva();
    }
}
