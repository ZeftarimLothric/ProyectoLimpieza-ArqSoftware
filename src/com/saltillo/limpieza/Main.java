package com.saltillo.limpieza;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.controllers.ConsultaActividadesController;
import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.controllers.LoginController;
import com.saltillo.limpieza.controllers.MainNavigationController;
import com.saltillo.limpieza.controllers.catalogos.ColoniaController;
import com.saltillo.limpieza.controllers.catalogos.CuadrillaController;
import com.saltillo.limpieza.controllers.catalogos.EmpleadoController;
import com.saltillo.limpieza.controllers.catalogos.TipoActividadController;
import com.saltillo.limpieza.controllers.catalogos.UsuarioController;
import com.saltillo.limpieza.database.ConexionBD;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.SqlActividadRepository;
import com.saltillo.limpieza.repositories.SqlDashboardRepository;
import com.saltillo.limpieza.repositories.SqlColoniaRepository;
import com.saltillo.limpieza.repositories.SqlCuadrillaRepository;
import com.saltillo.limpieza.repositories.SqlEmpleadoRepository;
import com.saltillo.limpieza.repositories.SqlEvidenciaRepository;
import com.saltillo.limpieza.repositories.SqlTipoActividadRepository;
import com.saltillo.limpieza.repositories.SqlUsuarioRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.ConsultaActividadesView;
import com.saltillo.limpieza.views.DashboardView;
import com.saltillo.limpieza.views.LoginView;
import com.saltillo.limpieza.views.MainShellView;
import com.saltillo.limpieza.views.actividades.ActividadDetalleView;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;
import com.saltillo.limpieza.views.catalogos.CatalogosView;
import com.saltillo.limpieza.views.catalogos.ColoniaView;
import com.saltillo.limpieza.views.catalogos.CuadrillaView;
import com.saltillo.limpieza.views.catalogos.EmpleadoView;
import com.saltillo.limpieza.views.catalogos.TipoActividadView;
import com.saltillo.limpieza.views.catalogos.UsuarioView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Punto de entrada principal de la aplicación.
 *
 * Composition Root (Inyección de Dependencias manual y cableado MVC completo):
 * - Arranque en LoginView (#7) con sesión obligatoria mediante SesionUsuario (#4, #19).
 * - Módulos reales conectados a MySQL: Dashboard (#17), Consulta (#16),
 *   Actividades/Evidencias/Colonias/Detalle/Observer (#10, #11, #12, #13, #14, #18),
 *   Cuadrillas (#9) y Catálogos (#8).
 * - Cierre seguro de observadores, conexión MySQL y sesión (#15).
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Continúa con el L&F por defecto si falla
        }

        SwingUtilities.invokeLater(() -> {
            if (SesionUsuario.getInstance().estaActiva()) {
                abrirShell();
            } else {
                mostrarLogin();
            }
        });
    }

    /**
     * Muestra la pantalla de acceso (Issue #7 / #15). Al autenticarse, abre el Shell principal.
     */
    public static void mostrarLogin() {
        LoginView loginView = new LoginView();
        loginView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarConexionSiExiste();
            }
        });
        new LoginController(loginView, new SqlUsuarioRepository(), Main::abrirShell);
        loginView.setVisible(true);
    }

    /**
     * Construye y muestra el Shell principal con los módulos y limpieza de cierre seguro (Issue #15).
     */
    public static void abrirShell() {
        if (!SesionUsuario.getInstance().estaActiva()) {
            mostrarLogin();
            return;
        }

        // 1. REPOSITORIOS REALES (MySQL)
        IDashboardRepository dashboardRepo = new SqlDashboardRepository();
        SqlCuadrillaRepository cuadrillaRepo = new SqlCuadrillaRepository();
        SqlColoniaRepository coloniaRepo = new SqlColoniaRepository();
        SqlUsuarioRepository usuarioRepo = new SqlUsuarioRepository();

        // 2. VISTAS DEL SISTEMA
        MainShellView shellView = new MainShellView();
        DashboardView dashboardView = new DashboardView();
        ConsultaActividadesView consultaView = new ConsultaActividadesView();
        ActividadListView actividadesView = new ActividadListView();
        CuadrillaView cuadrillasView = new CuadrillaView();

        // Módulo real de Catálogos (Issue #8)
        UsuarioView usuariosView = new UsuarioView();
        EmpleadoView empleadosView = new EmpleadoView();
        ColoniaView coloniasView = new ColoniaView();
        TipoActividadView tiposView = new TipoActividadView();

        CatalogosView catalogosView = new CatalogosView();
        catalogosView.agregarCatalogo("Usuarios", usuariosView);
        catalogosView.agregarCatalogo("Empleados", empleadosView);
        catalogosView.agregarCatalogo("Colonias", coloniasView);
        catalogosView.agregarCatalogo("Tipos de actividad", tiposView);

        // 3. REGISTRO MODULAR EN EL CONTENT HOST DEL SHELL (Desacoplamiento total)
        shellView.registrarModulo("DASHBOARD", dashboardView);
        shellView.registrarModulo("CONSULTA", consultaView);
        shellView.registrarModulo("ACTIVIDADES", actividadesView);
        shellView.registrarModulo("CUADRILLAS", cuadrillasView);
        shellView.registrarModulo("CATALOGOS", catalogosView);

        // 4. CONTROLADORES (Orquestación MVC y Patrón Observer)
        DashboardController dashboardController = new DashboardController(dashboardView, dashboardRepo);
        ConsultaActividadesController consultaController = new ConsultaActividadesController(consultaView,
                dashboardRepo);
        ActividadController actividadController = new ActividadController(
                actividadesView,
                new ActividadFormView(),
                new ActividadDetalleView(),
                new SqlActividadRepository(),
                new SqlEvidenciaRepository(),
                cuadrillaRepo,
                new EvidenciaStorageService(),
                new ColoniaSelectorController(new ColoniaSelectorDialog(), coloniaRepo));

        new CuadrillaController(cuadrillasView, cuadrillaRepo);
        new UsuarioController(usuariosView, usuarioRepo);
        new EmpleadoController(empleadosView, new SqlEmpleadoRepository());
        new ColoniaController(coloniasView, coloniaRepo);
        new TipoActividadController(tiposView, new SqlTipoActividadRepository());

        MainNavigationController navigationController = new MainNavigationController(shellView, true);
        navigationController.registrarLimpiezaAlCerrar(dashboardController::destruir);
        navigationController.registrarLimpiezaAlCerrar(consultaController::destruir);
        navigationController.registrarLimpiezaAlCerrar(Main::cerrarConexionSiExiste);
        navigationController.setAlCerrarSesionCallback(Main::mostrarLogin);

        // 5. NAVEGAR A LA VISTA INICIAL (Dashboard Ejecutivo) Y MOSTRAR
        navigationController.navegarA("DASHBOARD");
        shellView.setVisible(true);

        System.out.println("=========================================================");
        System.out.println("SISTEMA DE GESTION DE LIMPIEZA URBANA - SALTILLO COAHUILA");
        System.out.println("Módulo Integrante 4 (Shell, Dashboard, Consulta & Observer) ACTIVO.");
        System.out.println("=========================================================");
    }

    private static void cerrarConexionSiExiste() {
        if (Files.exists(Path.of(ConexionBD.RUTA_CONFIG))) {
            ConexionBD.getInstance().cerrar();
        }
    }
}
