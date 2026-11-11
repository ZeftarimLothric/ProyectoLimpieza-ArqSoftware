package com.saltillo.limpieza;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.controllers.ConsultaActividadesController;
import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.controllers.LoginController;
import com.saltillo.limpieza.controllers.MainNavigationController;
import com.saltillo.limpieza.controllers.catalogos.ColoniaController;
import com.saltillo.limpieza.controllers.catalogos.EmpleadoController;
import com.saltillo.limpieza.controllers.catalogos.TipoActividadController;
import com.saltillo.limpieza.controllers.catalogos.UsuarioController;
import com.saltillo.limpieza.database.ConexionBD;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.MockActividadRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.repositories.MockEvidenciaRepository;
import com.saltillo.limpieza.repositories.SqlColoniaRepository;
import com.saltillo.limpieza.repositories.SqlEmpleadoRepository;
import com.saltillo.limpieza.repositories.SqlTipoActividadRepository;
import com.saltillo.limpieza.repositories.SqlUsuarioRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.ConsultaActividadesView;
import com.saltillo.limpieza.views.DashboardView;
import com.saltillo.limpieza.views.LoginView;
import com.saltillo.limpieza.views.MainShellView;
import com.saltillo.limpieza.views.PlaceholderModuleView;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;
import com.saltillo.limpieza.views.catalogos.CatalogosView;
import com.saltillo.limpieza.views.catalogos.ColoniaView;
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
 * Composition Root (Inyección de Dependencias manual y cableado MVC).
 * 
 * Cumplimiento de Contratos e Issues:
 * - Issue #4: SesionUsuario y contratos base.
 * - Issue #16: Consulta de actividades con filtros (Requisito 11 de rúbrica).
 * 
 * =============================================================================
 * GUÍA DE INTEGRACIÓN PARA EL EQUIPO:
 * 1. INTEGRANTE 1 & 2 (Ximena & Eli - Issue #17):
 * Al terminar la BD MySQL, sustituir en la línea 48:
 * IDashboardRepository dashboardRepo = new MockDashboardRepository();
 * por:
 * IDashboardRepository dashboardRepo = new SqlDashboardRepository();
 * =============================================================================
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

        // 1. REPOSITORIO (TODO(#17): sustituir MockDashboardRepository por SqlDashboardRepository)
        IDashboardRepository dashboardRepo = new MockDashboardRepository();

        // 2. VISTAS DEL SISTEMA
        MainShellView shellView = new MainShellView();
        DashboardView dashboardView = new DashboardView();
        ConsultaActividadesView consultaView = new ConsultaActividadesView();
        ActividadListView actividadesView = new ActividadListView();

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

        // TODO(#9): reemplazar PlaceholderModuleView cuando esté listo CuadrillaView/CuadrillaController (#9)
        PlaceholderModuleView moduloCuadrillas = new PlaceholderModuleView(
                "CUADRILLAS",
                "Módulo: Catálogo de Cuadrillas y Jefes",
                "Integrante 2 (Eli - Issue #9)",
                "Mantenimiento de cuadrillas (CRUD), altas, bajas y cambios de jefes de cuadrilla, " +
                        "asignación de turnos y personal operativo.",
                dashboardRepo);

        // 3. REGISTRO MODULAR EN EL CONTENT HOST DEL SHELL (Desacoplamiento total)
        shellView.registrarModulo("DASHBOARD", dashboardView);
        shellView.registrarModulo("CONSULTA", consultaView);
        shellView.registrarModulo("ACTIVIDADES", actividadesView);
        shellView.registrarModulo("CUADRILLAS", moduloCuadrillas);
        shellView.registrarModulo("CATALOGOS", catalogosView);

        // 4. CONTROLADORES (Orquestación MVC y Patrón Observer)
        DashboardController dashboardController = new DashboardController(dashboardView, dashboardRepo);
        ConsultaActividadesController consultaController = new ConsultaActividadesController(consultaView,
                dashboardRepo);
        // TODO(#18): sustituir MockActividadRepository y MockEvidenciaRepository por Sql*Repository
        ActividadController actividadController = new ActividadController(
                actividadesView,
                new ActividadFormView(),
                new MockActividadRepository(),
                new MockEvidenciaRepository(),
                dashboardRepo,
                new EvidenciaStorageService(),
                new ColoniaSelectorController(new ColoniaSelectorDialog(), new SqlColoniaRepository()));

        new UsuarioController(usuariosView, new SqlUsuarioRepository());
        new EmpleadoController(empleadosView, new SqlEmpleadoRepository());
        new ColoniaController(coloniasView, new SqlColoniaRepository());
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
