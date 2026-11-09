package com.saltillo.limpieza;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.controllers.ConsultaActividadesController;
import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.controllers.MainNavigationController;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.MockActividadRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.repositories.MockEvidenciaRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.ConsultaActividadesView;
import com.saltillo.limpieza.views.DashboardView;
import com.saltillo.limpieza.views.MainShellView;
import com.saltillo.limpieza.views.PlaceholderModuleView;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;

import javax.swing.*;

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
 *    Al terminar la BD MySQL, sustituir en la línea 48:
 *        IDashboardRepository dashboardRepo = new MockDashboardRepository();
 *    por:
 *        IDashboardRepository dashboardRepo = new SqlDashboardRepository();
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
            // 1. REPOSITORIO (Inversión de dependencias mediante contrato IDashboardRepository)
            IDashboardRepository dashboardRepo = new MockDashboardRepository();

            // 2. VISTAS DEL SISTEMA
            MainShellView shellView = new MainShellView();
            DashboardView dashboardView = new DashboardView();
            ConsultaActividadesView consultaView = new ConsultaActividadesView();
            ActividadListView actividadesView = new ActividadListView();

            // Placeholders modulares para los compañeros de equipo
            PlaceholderModuleView moduloCuadrillas = new PlaceholderModuleView(
                    "CUADRILLAS",
                    "Módulo: Catálogo de Cuadrillas y Jefes",
                    "Integrante 2 (Acceso a Datos & CRUDs)",
                    "Mantenimiento de cuadrillas (CRUD), altas, bajas y cambios de jefes de cuadrilla, " +
                    "asignación de turnos y personal operativo.",
                    dashboardRepo
            );

            PlaceholderModuleView moduloColonias = new PlaceholderModuleView(
                    "COLONIAS",
                    "Módulo: Catálogo de Colonias (Sepomex Saltillo)",
                    "Integrante 1 (Base de Datos & Modelo Core)",
                    "Carga masiva e indexación del catálogo oficial de códigos postales y colonias " +
                    "del Municipio de Saltillo a partir de SEPOMEX.",
                    dashboardRepo
            );

            // 3. REGISTRO MODULAR EN EL CONTENT HOST DEL SHELL (Desacoplamiento total)
            shellView.registrarModulo("DASHBOARD", dashboardView);
            shellView.registrarModulo("CONSULTA", consultaView);
            shellView.registrarModulo("ACTIVIDADES", actividadesView);
            shellView.registrarModulo("CUADRILLAS", moduloCuadrillas);
            shellView.registrarModulo("COLONIAS", moduloColonias);

            // 4. CONTROLADORES (Orquestación MVC y Patrón Observer)
            DashboardController dashboardController = new DashboardController(dashboardView, dashboardRepo);
            ConsultaActividadesController consultaController = new ConsultaActividadesController(consultaView, dashboardRepo);
            ActividadController actividadController = new ActividadController(
                    actividadesView,
                    new ActividadFormView(),
                    new MockActividadRepository(),
                    new MockEvidenciaRepository(),
                    dashboardRepo,
                    new EvidenciaStorageService()
            );
            MainNavigationController navigationController = new MainNavigationController(shellView);

            // 5. NAVEGAR A LA VISTA INICIAL (Dashboard Ejecutivo) Y MOSTRAR
            navigationController.navegarA("DASHBOARD");
            shellView.setVisible(true);

            System.out.println("=========================================================");
            System.out.println("SISTEMA DE GESTION DE LIMPIEZA URBANA - SALTILLO COAHUILA");
            System.out.println("Módulo Integrante 4 (Shell, Dashboard, Consulta & Observer) ACTIVO.");
            System.out.println("=========================================================");
        });
    }
}
